/*
 * Set up build and verification targets, depending on what's enabled by the "targets" property.
 */

import org.gradle.api.GradleException
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.gradle.nativeplatform.platform.internal.DefaultOperatingSystem
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
}

kotlin {
    applyDefaultHierarchyTemplate()

    val enabledTargets = (property("targets") as String).split(",")
    enabledTargets.forEach { enabledTarget ->
        when (enabledTarget) {
            "all" -> {
                configureCommonTargets()
                configureAppleTargets()
                configureWindowsTargets()
                configureLinuxTargets()
            }
            "common" -> {
                configureCommonTargets()
            }
            "native", "host" -> {
                val os: DefaultOperatingSystem = DefaultNativePlatform.getCurrentOperatingSystem()
                when {
                    os.isMacOsX -> configureAppleTargets(enabledTarget == "host")
                    os.isWindows -> configureWindowsTargets()
                    os.isLinux -> configureLinuxTargets(enabledTarget == "host")
                }
            }
            else -> {
                throw GradleException(
                    "Property 'targets' must be a comma-separated list of " +
                        "'all', 'native', 'common', or 'host'; found '$targets'"
                )
            }
        }
    }
}

fun KotlinMultiplatformExtension.configureCommonTargets() {
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }

    js(IR) {
        browser {
            testTask {
                useKarma {
                    useChromiumHeadless()
                }
            }
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        nodejs()
    }

    sourceSets {
        jvmTest.dependencies {
            implementation(kotlin("test-junit"))
        }
        jsTest.dependencies {
            implementation(kotlin("test-js"))
        }
    }
}

fun KotlinMultiplatformExtension.configureAppleTargets(hostOnly: Boolean = false) {
    macosX64()
    macosArm64()

    if (!hostOnly) {
        iosX64()
        iosArm64()
        iosSimulatorArm64()

        watchosX64()
        watchosArm32()
        watchosArm64()
        watchosSimulatorArm64()
        watchosDeviceArm64()

        tvosX64()
        tvosArm64()
        tvosSimulatorArm64()
    }
}

fun KotlinMultiplatformExtension.configureWindowsTargets() {
    mingwX64()
}

fun KotlinMultiplatformExtension.configureLinuxTargets(hostOnly: Boolean = false) {
    val linuxTargets = mutableListOf<KotlinNativeTarget>()

    val arch = System.getProperty("os.arch")
    if (!hostOnly || arch == "amd64" || arch == "x86_64") {
        linuxTargets.add(linuxX64())
    }
    if (!hostOnly || arch == "aarch64") {
        linuxTargets.add(linuxArm64())
    }

    linuxTargets.forEach { linuxTarget ->
        linuxTarget.compilations.getByName("main") {
            @Suppress("UNUSED_VARIABLE")
            val uninorm by cinterops.creating
        }
    }
}

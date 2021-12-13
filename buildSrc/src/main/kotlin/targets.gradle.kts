/*
* Set up build and verification targets, depending on what's enabled by the "targets" property.
*/

import org.gradle.api.GradleException
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.gradle.nativeplatform.platform.internal.DefaultOperatingSystem
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet.Companion.COMMON_MAIN_SOURCE_SET_NAME
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform")
}

kotlin {
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
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
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

    sourceSets {
        @Suppress("UNUSED_VARIABLE")
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        @Suppress("UNUSED_VARIABLE")
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

fun KotlinMultiplatformExtension.configureAppleTargets(hostOnly: Boolean = false) {
    val darwinTargets = mutableListOf<KotlinNativeTarget>(
        macosX64(),
        macosArm64()
    )

    if (!hostOnly) {
        darwinTargets.apply {
            add(iosX64())
            add(iosSimulatorArm64())
            add(iosArm64())

            add(watchosX64())
            add(watchosSimulatorArm64())
            add(watchosArm32())
            add(watchosArm64())

            add(tvosX64())
            add(tvosSimulatorArm64())
            add(tvosArm64())
        }
    }

    sourceSets {
        val darwinMain by creating { dependsOn(getByName(COMMON_MAIN_SOURCE_SET_NAME)) }

        darwinTargets.forEach { darwinTarget ->
            getByName("${darwinTarget.name}Main") { dependsOn(darwinMain) }
        }
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

    sourceSets {
        val linuxMain by creating { dependsOn(getByName(COMMON_MAIN_SOURCE_SET_NAME)) }

        linuxTargets.forEach { linuxTarget ->
            getByName("${linuxTarget.name}Main") { dependsOn(linuxMain) }
        }
    }
}

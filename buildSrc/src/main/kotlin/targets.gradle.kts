/*
* Set up build and verification targets, depending on what's enabled by the "targets" property.
*/

import org.gradle.api.GradleException
import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.gradle.nativeplatform.platform.internal.DefaultOperatingSystem
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget

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
                    os.isLinux -> configureLinuxTargets()
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
    val darwinTargets = mutableListOf<KotlinTarget>(macosX64())

    if (!hostOnly) {
        darwinTargets.apply {
            add(iosX64())
            add(iosArm64())

            add(watchosX64())
            add(watchosArm32())
            add(watchosArm64())

            add(tvosX64())
            add(tvosArm64())
        }
    }

    sourceSets {
        val darwinMain by creating { dependsOn(getByName("commonMain")) }

        darwinTargets.forEach { darwinTarget ->
            getByName("${darwinTarget.name}Main") { dependsOn(darwinMain) }
        }
    }
}

fun KotlinMultiplatformExtension.configureWindowsTargets() {
    mingwX64()
}

fun KotlinMultiplatformExtension.configureLinuxTargets() {
    linuxX64 {
        val main by compilations.getting
        @Suppress("UNUSED_VARIABLE")
        val uninorm by main.cinterops.creating {
            defFile = project.file("src/linuxX64Interop/cinterop/uninorm.def")
        }
    }
}

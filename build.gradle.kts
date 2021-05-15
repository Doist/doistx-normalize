import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform
import org.gradle.nativeplatform.platform.internal.DefaultOperatingSystem

plugins {
    kotlin("multiplatform") version "1.5.0"
    id("publish")
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

group = property("GROUP") ?: throw GradleException("Missing group")
version = System.getenv("PUBLISH_VERSION") ?: "0.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
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
            commonWebpackConfig {
                cssSupport.enabled = false
            }
            testTask {
                useKarma {
                    useChromiumHeadless()
                }
            }
        }
    }
    ios()
    macosX64()
    mingwX64()
    linuxX64 {
        val main by compilations.getting
        val interop by main.cinterops.creating {
            defFile = project.file("src/linuxX64Interop/cinterop/uninorm.def")
        }

        binaries {
            executable()
        }
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val nativeDarwinMain by creating {
            dependsOn(commonMain)
        }
        val iosArm64Main by getting {
            dependsOn(nativeDarwinMain)
        }
        val iosX64Main by getting {
            dependsOn(nativeDarwinMain)
        }
        val macosX64Main by getting {
            dependsOn(nativeDarwinMain)
        }
        val mingwX64Main by getting
        val linuxX64Main by getting
    }
}

// Generate stubs before compiling for Linux.
tasks.findByName("compileKotlinLinuxX64")?.mustRunAfter("cinteropInteropLinuxX64")

// Disable cross-compilation/publication of the Linux target.
val os: DefaultOperatingSystem = DefaultNativePlatform.getCurrentOperatingSystem()
tasks.matching { it.name.contains("linux", true) }.configureEach { onlyIf { os.isLinux } }

// Split compilation and test in CI.
// Apple-specific targets on macOS, Windows-specific targets on Windows, everything else on Linux.
tasks.register("ciCompile") {
    group = "build"
    when {
        os.isLinux -> dependsOn(
            tasks.matching { it.name.matches(Regex("^compileKotlin(?!Ios|Macos|Mingw).*$")) }.map { it.name })
        os.isMacOsX -> dependsOn(
            tasks.matching { it.name.matches(Regex("^compileKotlin(Ios|Macos).*$")) }.map { it.name })
        os.isWindows -> dependsOn(
            tasks.matching { it.name.matches(Regex("^compileKotlin(Mingw).*$")) }.map { it.name })
    }
}
tasks.register("ciTests") {
    group = "verification"
    when {
        os.isLinux -> dependsOn(
            tasks.matching { it.name.matches(Regex("^(?!ios|macos|mingw).*Test$")) }.map { it.name })
        os.isMacOsX -> dependsOn(
            tasks.matching { it.name.matches(Regex("^(ios|macos).*Test$")) }.map { it.name })
        os.isWindows -> dependsOn(
            tasks.matching { it.name.matches(Regex("^(mingw).*Test$")) }.map { it.name })
    }
}

// Publish common targets from the main host only.
// Set the `publishCommonTargets` project property, e.g., via `-PpublishCommonTargets=true`.
// See: https://docs.gradle.org/current/userguide/build_environment.html#sec:project_properties
val commonPublications = arrayOf("jvm", "js", "kotlinMultiplatform")
publishing.publications.matching { commonPublications.contains(it.name) }.all {
    tasks.withType<AbstractPublishToMaven>()
        .matching { it.publication == this@all }
        .configureEach { onlyIf { findProperty("publishCommonTargets") == "true" } }
}

// Leverage Gradle Nexus Publish Plugin to close and release staging repositories,
// covering the last part of the release process to Maven Central.
nexusPublishing {
    repositories {
        sonatype {
            username.set(extra["ossrh.username"]?.toString())
            password.set(extra["ossrh.password"]?.toString())
        }
    }
}

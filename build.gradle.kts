import org.gradle.nativeplatform.platform.internal.DefaultNativePlatform

val os = DefaultNativePlatform.getCurrentOperatingSystem()

plugins {
    kotlin("multiplatform") version "1.5.0"
    id("maven-publish")
}

group = "com.doist.x.normalize"
version = "1.0.0-SNAPSHOT"

publishing {
    repositories {
        maven {
            name = "sonatype"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("SONATYPE_NEXUS_USERNAME")
                password = System.getenv("SONATYPE_NEXUS_PASSWORD")
            }
        }
    }

    publications {
        withType<MavenPublication> {
            pom {
                name.set("doistx-normalize")
                description.set("KMP library for string normalization ")
                url.set("https://github.com/Doist/doistx-normalize")

                licenses {
                    license {
                        name.set("MIT")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("goncalo")
                        name.set("Gonçalo Silva")
                        email.set("goncalo@doist.com")
                    }
                }
                scm {
                    url.set("https://github.com/Doist/doistx-normalize")
                }
            }
        }

        // Publish common targets from the main host only.
        // Set the `publishCommonTargets` project property, e.g., via `-PpublishCommonTargets=true`.
        // See: https://docs.gradle.org/current/userguide/build_environment.html#sec:project_properties
        val commonPublications = arrayOf("jvm", "js", "kotlinMultiplatform")
        matching { it.name in commonPublications }.all {
            tasks.withType<AbstractPublishToMaven>()
                .matching { it.publication == this@all }
                .configureEach { onlyIf { findProperty("publishCommonTargets") == "true" } }
        }
    }
}

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
tasks.matching { it.name.contains("linux", true) }.configureEach { onlyIf { os.isLinux } }

// Split tests in CI.
// Apple-specific targets on macOS, Windows-specific targets on Windows, everything else on Linux.
tasks.register("ciTests") {
    group = "verification"
    when {
        os.isLinux -> dependsOn(
            tasks.matching { it.name.matches(Regex("!(ios|macos|mingw).*Test")) }.map { it.name })
        os.isMacOsX -> dependsOn(
            tasks.matching { it.name.matches(Regex("(ios|macos).*Test")) }.map { it.name })
        os.isWindows -> dependsOn(
            tasks.matching { it.name.matches(Regex("(mingw).*Test")) }.map { it.name })
    }
}

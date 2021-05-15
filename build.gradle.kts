import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstance

plugins {
    kotlin("multiplatform") version "1.5.0"
    id("disable-cross-compile")
    id("ci")
    id("publish")
    id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
}

group = "com.doist.x"
val version: String by project
setVersion(version)

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

// TODO: Move to buildSrc/src/main/kotlin/publish.gradle.kts once the plugin supports it.
// Leverage Gradle Nexus Publish Plugin to close and release staging repositories,
// covering the last part of the release process to Maven Central.
nexusPublishing {
    repositories {
        sonatype {
            val sonatypeStagingProfileId: String by project
            stagingProfileId.set(sonatypeStagingProfileId)
            val credentials =
                publishing.repositories.firstIsInstance<AuthenticationSupported>().credentials
            username.set(credentials.username)
            password.set(credentials.password)
        }
    }
}

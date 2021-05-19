plugins {
    kotlin("multiplatform") version "1.5.0"
    id("disable-cross-compile") apply false
    id("ci") apply false
    id("publish") apply false
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

    iosX64()
    iosArm64()

    watchosX64()
    watchosArm64()

    tvosX64()
    tvosArm64()

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

        val darwinMain by creating {
            dependsOn(commonMain)
        }

        val iosArm64Main by getting {
            dependsOn(darwinMain)
        }
        val iosX64Main by getting {
            dependsOn(darwinMain)
        }

        val watchosArm64Main by getting {
            dependsOn(darwinMain)
        }
        val watchosX64Main by getting {
            dependsOn(darwinMain)
        }

        val tvosArm64Main by getting {
            dependsOn(darwinMain)
        }
        val tvosX64Main by getting {
            dependsOn(darwinMain)
        }

        val macosX64Main by getting {
            dependsOn(darwinMain)
        }

        val mingwX64Main by getting

        val linuxX64Main by getting
    }
}

// Apply plugins now that targets were created.
apply(plugin = "disable-cross-compile")
apply(plugin = "ci")
apply(plugin = "publish")

// TODO: Move to buildSrc/src/main/kotlin/publish.gradle.kts once the plugin supports it.
// Leverage Gradle Nexus Publish Plugin to create, close and release staging repositories,
// covering the last part of the release process to Maven Central.
nexusPublishing {
    repositories {
        sonatype {
            // Read `ossrhUsername` and `ossrhPassword` properties.
            // DO NOT ADD THESE TO SOURCE CONTROL. Store them in your system properties,
            // or pass them in using ORG_GRADLE_PROJECT_* environment variables.
            val ossrhUsername: String? by project
            val ossrhPassword: String? by project
            val ossrhStagingProfileId: String by project
            username.set(ossrhUsername)
            password.set(ossrhPassword)
            stagingProfileId.set(ossrhStagingProfileId)
        }
    }
}

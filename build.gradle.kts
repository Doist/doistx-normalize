import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("targets")
    id("cinterop")
    id("publish")
    id("com.goncalossilva.resources") version "0.4.0"
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(11)

    explicitApi()

    sourceSets {
        all {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }

        @Suppress("UNUSED_VARIABLE")
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("com.goncalossilva:resources:0.4.0")
            }
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(8)
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.contracts.ExperimentalContracts"
    }
}

// Sanity check before attempting to publish root target without having all targets enabled.
tasks.matching { it.name.startsWith("publishKotlinMultiplatform") }.configureEach {
    doFirst {
        require(findProperty("targets") == "all") {
            "Configuration is set to publish root target without all targets enabled."
        }
    }
}

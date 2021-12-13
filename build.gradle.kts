import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("targets")
    id("cinterop")
    id("publish")
}

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(8)
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = "1.8"
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

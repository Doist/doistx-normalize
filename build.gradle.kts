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

// Sanity check before attempting to publish root target without having all targets enabled.
tasks.named("publishKotlinMultiplatform") {
    doFirst {
        if (findProperty("targets") != "all") {
            throw IllegalStateException(
                "Configuration is set to publish root target without all targets enabled.")
        }
    }
}

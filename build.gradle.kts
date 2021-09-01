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
tasks.matching { it.name.startsWith("publishKotlinMultiplatform") }
    .configureEach {
        doFirst {
            require(findProperty("targets") == "all") {
                "Configuration is set to publish root target without all targets enabled."
            }
        }
    }

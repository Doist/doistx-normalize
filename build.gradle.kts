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

plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.24")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.9.20")
    implementation("io.github.gradle-nexus:publish-plugin:2.0.0")
}

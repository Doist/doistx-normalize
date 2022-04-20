plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.6.20")
    implementation("io.github.gradle-nexus:publish-plugin:1.1.0")
}

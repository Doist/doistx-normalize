plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.22")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:1.8.20")
    implementation("io.github.gradle-nexus:publish-plugin:1.3.0")
}

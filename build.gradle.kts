import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootEnvSpec

plugins {
    kotlin("multiplatform")
    id("targets")
    id("cinterop")
    id("publish")
    id("com.goncalossilva.resources") version "0.14.4"
}

repositories {
    mavenCentral()
}

kotlin {
    explicitApi()

    jvmToolchain(11)

    sourceSets {
        all {
            languageSettings.optIn("kotlinx.cinterop.ExperimentalForeignApi")
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("com.goncalossilva:resources:0.14.4")
            }
        }
    }
}

plugins.withType<YarnPlugin> {
    the<YarnRootEnvSpec>().apply {
        yarnLockMismatchReport = YarnLockMismatchReport.WARNING
        yarnLockAutoReplace = true
    }
}

// Sanity check before attempting to publish root target without having all targets enabled.
tasks.matching { it.name.startsWith("publishKotlinMultiplatform") }.configureEach {
    doFirst {
        val enabledTargets = findProperty("targets")
            ?.toString()
            ?.split(",")
            ?.map(String::trim)
            ?.filter(String::isNotEmpty)
            .orEmpty()

        require(enabledTargets.contains("all")) {
            "Configuration is set to publish root target without 'all' targets enabled."
        }
    }
}

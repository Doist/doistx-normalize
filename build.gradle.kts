import org.gradle.kotlin.dsl.the
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootEnvSpec

plugins {
    kotlin("multiplatform")
    id("targets")
    id("cinterop")
    id("publish")
    id("com.goncalossilva.resources") version "0.10.0"
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
            }
        }

        val nonWasmTest by creating {
            dependsOn(commonTest)
            dependencies {
                implementation("com.goncalossilva:resources:0.10.0")
            }
        }

        jvmTest { dependsOn(nonWasmTest) }
        jsTest { dependsOn(nonWasmTest) }
        nativeTest { dependsOn(nonWasmTest) }
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
        require(findProperty("targets") == "all") {
            "Configuration is set to publish root target without all targets enabled."
        }
    }
}

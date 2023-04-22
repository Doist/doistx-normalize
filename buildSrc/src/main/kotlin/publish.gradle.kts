/*
 * Set up publishing with Maven Central. Useful resources:
 * - https://kotlinlang.org/docs/mpp-publish-lib.html
 * - https://central.sonatype.org/publish/ (esp. the GPG section)
 * - https://getstream.io/blog/publishing-libraries-to-mavencentral-2021/
 * - https://dev.to/kotlin/how-to-build-and-publish-a-kotlin-multiplatform-library-going-public-4a8k
 */

import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.`maven-publish`
import org.gradle.kotlin.dsl.signing
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("org.jetbrains.dokka")
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin")
}

group = "com.doist.x"
version = property("version") as String

// TODO: Remove when https://youtrack.jetbrains.com/issue/KT-46466 is fixed.
val signingTasks = tasks.withType<Sign>()
tasks.withType<AbstractPublishToMaven>().configureEach {
    dependsOn(signingTasks)
}

val dokkaHtmlJavadocJar by tasks.registering(Jar::class) {
    dependsOn(tasks.dokkaHtml)
    from(tasks.dokkaHtml.flatMap { it.outputDirectory })
    archiveClassifier.set("javadoc")
}

// Setup publishing environment.
publishing {
    // Configure all publications.
    publications.withType<MavenPublication> {
        val pomDescription: String by project
        val pomUrl: String by project
        val pomLicenseName: String by project
        val pomLicenseUrl: String by project
        val pomScmUrl: String by project
        val pomScmConnection: String by project
        val pomScmDeveloperConnection: String by project
        val pomDeveloperId: String by project
        val pomDeveloperName: String by project

        // Publish docs with each artifact.
        artifact(dokkaHtmlJavadocJar)

        // Provide information requited by Maven Central.
        pom {
            name.set(rootProject.name)
            description.set(pomDescription)
            url.set(pomUrl)

            licenses {
                license {
                    name.set(pomLicenseName)
                    url.set(pomLicenseUrl)
                }
            }

            scm {
                url.set(pomScmUrl)
                connection.set(pomScmConnection)
                developerConnection.set(pomScmDeveloperConnection)
            }

            developers {
                developer {
                    id.set(pomDeveloperId)
                    name.set(pomDeveloperName)
                }
            }
        }
    }
}

// Sign artifacts.
// Use `signingKey` and `signingPassword` properties if provided.
// Otherwise, default to `signing.keyId`, `signing.password` and `signing.secretKeyRingFile`.
signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
    sign(publishing.publications)
}

// Leverage Gradle Nexus Publish Plugin to create, close and release staging repositories,
// covering the last part of the release process to Maven Central.
nexusPublishing {
    repositories {
        sonatype {
            // Read `ossrhUsername` and `ossrhPassword` properties.
            // DO NOT ADD THESE TO SOURCE CONTROL. Store them in your system properties,
            // or pass them in using ORG_GRADLE_PROJECT_* environment variables.
            val ossrhUsername: String? by project
            val ossrhPassword: String? by project
            val ossrhStagingProfileId: String? by project
            username.set(ossrhUsername)
            password.set(ossrhPassword)
            stagingProfileId.set(ossrhStagingProfileId)
        }
    }
}

// Publish root target only when explicitly specified, since it can only be published once.
tasks.matching { it.name.startsWith("publishKotlinMultiplatform") }
    .configureEach { onlyIf { findProperty("publishRootTarget") == "true" } }

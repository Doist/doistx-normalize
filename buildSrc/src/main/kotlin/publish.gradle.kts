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

plugins {
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

val javadocJar by tasks.registering(Jar::class) {
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
        artifact(javadocJar)

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
        // See https://central.sonatype.org/publish/publish-portal-ossrh-staging-api/#configuration
        sonatype {
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
        }
    }
}

// Publish root target only when explicitly specified, since it can only be published once.
tasks.matching { it.name.startsWith("publishKotlinMultiplatform") }
    .configureEach { onlyIf { findProperty("publishRootTarget") == "true" } }

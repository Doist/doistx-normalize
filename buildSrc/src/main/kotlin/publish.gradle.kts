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

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

// Setup publishing environment.
publishing {
    // Configure all publications.
    @Suppress("LocalVariableName")
    publications.withType<MavenPublication> {
        val pomDescription = property("pom.description") as String
        val pomUrl = property("pom.url") as String
        val pomLicenseName = property("pom.license.name") as String
        val pomLicenseUrl = property("pom.license.url") as String
        val pomScmUrl = property("pom.scm.url") as String
        val pomScmConnection = property("pom.scm.connection") as String
        val pomScmDeveloperConnection = property("pom.scm.developerConnection") as String
        val pomDeveloperId = property("pom.developer.id") as String
        val pomDeveloperName = property("pom.developer.name") as String

        // Publish javadoc with each artifact.
        artifact(javadocJar.get())

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
// Use `signing.key` and `signing.password` properties if provided.
// Otherwise, default to `signing.keyId`, `signing.password` and `signing.secretKeyRingFile`.
signing {
    val signingKey = findProperty("signing.key") as String?
    val signingPassword = findProperty("signing.password") as String?
    if (signingKey != null && signingPassword != null) {
        useInMemoryPgpKeys(signingKey, signingPassword)
    }
    sign(publishing.publications)
}

// Publish root target when explicitly specified, since it can only be published once.
publishing.publications.matching { it.name == "kotlinMultiplatform" }.all {
    tasks.withType<AbstractPublishToMaven>()
        .matching { it.publication == this@all }
        .configureEach { onlyIf { findProperty("publishRootTarget") == "true" } }
}

// Leverage Gradle Nexus Publish Plugin to create, close and release staging repositories,
// covering the last part of the release process to Maven Central.
nexusPublishing {
    repositories {
        sonatype {
            // Read `ossrhUsername` and `ossrhPassword` properties.
            // DO NOT ADD THESE TO SOURCE CONTROL. Store them in your system properties,
            // or pass them in using ORG_GRADLE_PROJECT_* environment variables.
            val ossrhUsername = findProperty("ossrh.username") as String?
            val ossrhPassword = findProperty("ossrh.password") as String?
            val ossrhStagingProfileId = findProperty("ossrh.stagingProfileId") as String?
            username.set(ossrhUsername)
            password.set(ossrhPassword)
            stagingProfileId.set(ossrhStagingProfileId)
        }
    }
}

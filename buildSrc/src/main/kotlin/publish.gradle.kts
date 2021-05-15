import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.`maven-publish`
import org.gradle.kotlin.dsl.signing

/*
 * Sets up publishing with Maven Central. Useful resources:
 * - https://kotlinlang.org/docs/mpp-publish-lib.html
 * - https://central.sonatype.org/publish/ (esp. the GPG section)
 * - https://getstream.io/blog/publishing-libraries-to-mavencentral-2021/
 * - https://dev.to/kotlin/how-to-build-and-publish-a-kotlin-multiplatform-library-going-public-4a8k
 */

plugins {
    `maven-publish`
    signing
}

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

// Setup publishing environment.
publishing {
    // Configure maven central repository.
    repositories {
        maven {
            name = "sonatype"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                // Read `ossrhUsername` and `ossrhPassword` properties.
                // DO NOT ADD THESE TO SOURCE CONTROL. Store them in your system properties,
                // or pass them in using ORG_GRADLE_PROJECT_* environment variables.
                val ossrhUsername: String? by project
                val ossrhPassword: String? by project
                username = ossrhUsername
                password = ossrhPassword
            }
        }
    }

    // Configure all publications.
    @Suppress("LocalVariableName")
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

// Publish common targets from the main host only.
// Set the `publishCommonTargets` project property, e.g., via `-PpublishCommonTargets=true`.
// See: https://docs.gradle.org/current/userguide/build_environment.html#sec:project_properties
val commonPublications = arrayOf("jvm", "js", "wasm32", "kotlinMultiplatform")
publishing.publications.matching { commonPublications.contains(it.name) }.all {
    tasks.withType<AbstractPublishToMaven>()
        .matching { it.publication == this@all }
        .configureEach { onlyIf { findProperty("publishCommonTargets") == "true" } }
}

import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.`maven-publish`
import org.gradle.kotlin.dsl.signing
import java.util.Properties

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

// Initialize extras from environment variables.
extra["ossrh.username"] = System.getenv("OSSRH_USERNAME")
extra["ossrh.password"] = System.getenv("OSSRH_PASSWORD")
extra["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
extra["signing.secretKey"] = System.getenv("SIGNING_SECRET_KEY")
extra["signing.secretKeyRingFile"] = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
extra["signing.password"] = System.getenv("SIGNING_PASSWORD")

// Read from publish.properties, without overriding.
val secretPropsFile = project.rootProject.file("publish.properties")
if (secretPropsFile.exists()) {
    secretPropsFile.reader()
        .use { Properties().apply { load(it) } }
        .filter { (key, _) -> extra[key.toString()] == null }
        .onEach { (key, value) -> extra[key.toString()] = value.toString() }
}

// Publish JavaDoc with each artifact.
val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

publishing {
    // Configure maven central repository.
    repositories {
        maven {
            name = "sonatype"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = extra["ossrh.username"]?.toString()
                password = extra["ossrh.password"]?.toString()
            }
        }
    }

    // Configure all publications.
    @Suppress("LocalVariableName")
    publications.withType<MavenPublication> {
        val GROUP: String by project
        val POM_DESCRIPTION: String by project
        val POM_URL: String by project
        val POM_LICENSE_NAME: String by project
        val POM_LICENSE_URL: String by project
        val POM_SCM_URL: String by project
        val POM_SCM_CONNECTION: String by project
        val POM_SCM_DEVELOPER_CONNECTION: String by project
        val POM_DEVELOPER_ID: String by project
        val POM_DEVELOPER_NAME: String by project

        groupId = GROUP
        artifactId = rootProject.name
        version = System.getenv("PUBLISH_VERSION") ?: "SNAPSHOT"

        // Stub javadoc.jar artifact.
        artifact(javadocJar.get())

        // Provide information requited by Maven Central.
        pom {
            name.set(rootProject.name)
            description.set(POM_DESCRIPTION)
            url.set(POM_URL)

            licenses {
                license {
                    name.set(POM_LICENSE_NAME)
                    url.set(POM_LICENSE_URL)
                }
            }

            scm {
                url.set(POM_SCM_URL)
                connection.set(POM_SCM_CONNECTION)
                developerConnection.set(POM_SCM_DEVELOPER_CONNECTION)
            }

            developers {
                developer {
                    id.set(POM_DEVELOPER_ID)
                    name.set(POM_DEVELOPER_NAME)
                }
            }
        }
    }
}

// Sign artifacts. extra["signing.*"] properties will be used.
signing {
    if (extra["signing.keyId"] == null || extra["signing.password"] == null ||
        extra["signing.secretKey"] == null && extra["signing.secretKeyRingFile"] == null) {
        logger.info("Signing configuration is missing. Publishing will not work.")
        return@signing
    }
    if (extra["signing.secretKey"] != null) {
        useInMemoryPgpKeys(
            extra["signing.keyId"]?.toString(),
            extra["signing.secretKey"]?.toString(),
            extra["signing.password"]?.toString()
        )
    }
    sign(publishing.publications)
}

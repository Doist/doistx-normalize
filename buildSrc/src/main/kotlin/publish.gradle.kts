import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.kotlin.dsl.`maven-publish`
import org.gradle.kotlin.dsl.signing
import java.util.Properties

plugins {
    `maven-publish`
    signing
}

// Initialize extras.
extra["ossrh.username"] = null
extra["ossrh.password"] = null
extra["signing.keyId"] = null
extra["signing.secretKey"] = null
extra["signing.secretKeyRingFile"] = null
extra["signing.password"] = null

// Read from signing.properties.
val secretPropsFile = project.rootProject.file("signing.properties")
if (secretPropsFile.exists()) {
    secretPropsFile.reader().use {
        Properties().apply {
            load(it)
        }
    }.onEach { (name, value) ->
        extra[name.toString()] = value.toString()
    }
}

// Read from environment variables.
extra["ossrh.username"] = System.getenv("OSSRH_USERNAME") ?: extra["ossrh.username"]
extra["ossrh.password"] = System.getenv("OSSRH_PASSWORD") ?: extra["ossrh.password"]
extra["signing.keyId"] = System.getenv("SIGNING_KEY_ID") ?: extra["signing.keyId"]
extra["signing.secretKey"] = System.getenv("SIGNING_SECRET_KEY") ?: extra["signing.secretKey"]
extra["signing.secretKeyRingFile"] =
    System.getenv("SIGNING_SECRET_KEY_RING_FILE") ?: extra["signing.secretKeyRingFile"]
extra["signing.password"] = System.getenv("SIGNING_PASSWORD") ?: extra["signing.password"]

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
    publications.withType<MavenPublication> {
        groupId = "com.doist.x.normalize"
        artifactId = "doistx-normalize"
        version = System.getenv("PUBLISH_VERSION")

        // Stub javadoc.jar artifact.
        artifact(javadocJar.get())

        // Provide information requited by Maven Central.
        pom {
            name.set("doistx-normalize")
            description.set("KMP library for string unicode normalization")
            url.set("https://github.com/Doist/doistx-normalize")

            licenses {
                license {
                    name.set("MIT")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }
            developers {
                developer {
                    id.set("goncalo")
                    name.set("Gonçalo Silva")
                    email.set("goncalo@doist.com")
                }
            }
            scm {
                url.set("https://github.com/Doist/doistx-normalize")
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

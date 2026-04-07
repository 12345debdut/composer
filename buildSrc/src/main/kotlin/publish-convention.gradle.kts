/**
 * Precompiled script plugin for publishing Android library modules to Maven
 * Central via the Central Publisher Portal (ossrh-staging-api.central.sonatype.com).
 *
 * Structure mirrors the anchor-di publishing playbook §5.2 verbatim, with
 * one Android-specific addition: the "release" MavenPublication is created
 * inside afterEvaluate (because AGP's "release" SoftwareComponent only
 * exists post-evaluate). Everything else — POM configureEach at top level,
 * useInMemoryPgpKeys at top level, signing.sign(publications) inside
 * afterEvaluate — is identical to the playbook.
 *
 * Apply via: apply(plugin = "publish-convention")
 *
 * The module's own build.gradle.kts must:
 *   - Apply com.android.library
 *   - Declare android { publishing { singleVariant("release") { withSourcesJar() } } }
 */

val libraryGroup: String =
    project.findProperty("LIBRARY_GROUP") as? String ?: "io.github.example"
val libraryVersion: String =
    project.findProperty("LIBRARY_VERSION") as? String
        ?: project.findProperty("VERSION_NAME") as? String
        ?: "0.1.0"

group = libraryGroup
version = libraryVersion

val pomName: String = project.findProperty("POM_NAME") as? String ?: project.name
val pomDescription: String =
    project.findProperty("POM_DESCRIPTION") as? String ?: "An Android library."
val pomUrl: String =
    project.findProperty("POM_URL") as? String ?: "https://github.com/example/repo"
val pomScmUrl: String = project.findProperty("POM_SCM_URL") as? String ?: pomUrl
val pomScmConnection: String =
    project.findProperty("POM_SCM_CONNECTION") as? String
        ?: "scm:git:git://github.com/example/repo.git"
val pomScmDevConnection: String =
    project.findProperty("POM_SCM_DEV_CONNECTION") as? String
        ?: "scm:git:ssh://git@github.com/example/repo.git"
val pomLicenseName: String =
    project.findProperty("POM_LICENSE_NAME") as? String ?: "The Apache License, Version 2.0"
val pomLicenseUrl: String =
    project.findProperty("POM_LICENSE_URL") as? String
        ?: "https://www.apache.org/licenses/LICENSE-2.0.txt"
val pomDeveloperId: String = project.findProperty("POM_DEVELOPER_ID") as? String ?: "example"
val pomDeveloperName: String = project.findProperty("POM_DEVELOPER_NAME") as? String ?: "Example"
val pomDeveloperEmail: String =
    project.findProperty("POM_DEVELOPER_EMAIL") as? String ?: ""
val pomDeveloperUrl: String =
    project.findProperty("POM_DEVELOPER_URL") as? String ?: "https://github.com/example"
val pomInceptionYear: String =
    project.findProperty("POM_INCEPTION_YEAR") as? String ?: "2025"

plugins.apply("maven-publish")

// ── Signing config — RESOLVED FROM ROOT PROJECT ─────────────────────────────
// Subprojects often cannot see ~/.gradle/gradle.properties directly. Looking
// up via rootProject.findProperty() guarantees the values are visible. This
// is the cause of the cryptic "no configured signatory" error otherwise.
val root = rootProject

val signingKeyId: String? =
    (root.findProperty("signing.keyId") as? String)?.ifBlank { null }
        ?: (root.findProperty("signingInMemoryKeyId") as? String)?.ifBlank { null }
        ?: System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKeyId")?.ifBlank { null }

val signingPassword: String? =
    (root.findProperty("signing.password") as? String)?.ifBlank { null }
        ?: (root.findProperty("signingInMemoryKeyPassword") as? String)?.ifBlank { null }
        ?: System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKeyPassword")?.ifBlank { null }

val keyFilePath: String? =
    (root.findProperty("signing.keyFile") as? String)?.ifBlank { null }
        ?: (root.findProperty("signing.secretKeyRingFile") as? String)?.ifBlank { null }

val keyFileAbsolutePath: String? =
    keyFilePath?.let { path ->
        val normalized =
            if (path.startsWith("~")) {
                System.getProperty("user.home", "").trimEnd('/') + path.drop(1)
            } else {
                path
            }
        val file = java.io.File(normalized)
        if (file.exists()) file.absolutePath else null
    }

val hasSigningKeyFromFile =
    keyFileAbsolutePath != null && signingKeyId != null && signingPassword != null
val signingKeyContent: String? =
    (root.findProperty("signingInMemoryKey") as? String)?.takeIf { it.isNotBlank() }
        ?: System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKey")?.takeIf { it.isNotBlank() }
val hasSigningKeyFromContent = signingKeyContent != null && signingPassword != null
val hasSigningKey = hasSigningKeyFromFile || hasSigningKeyFromContent

if (hasSigningKey) {
    if (hasSigningKeyFromFile) {
        // Gradle's signing plugin reads these extra properties natively for
        // the file-based signatory. Must be set BEFORE the signing plugin
        // is applied so its convention captures them.
        project.extra["signing.keyId"] = signingKeyId!!
        project.extra["signing.password"] = signingPassword!!
        project.extra["signing.secretKeyRingFile"] = keyFileAbsolutePath!!
    }
    plugins.apply("signing")
}

// ── Sonatype credentials (Central Portal user token) ────────────────────────
val sonatypeUsername: String? =
    (project.findProperty("SONATYPE_USERNAME") as? String)?.ifBlank { null }
        ?: System.getenv("ORG_GRADLE_PROJECT_SONATYPE_USERNAME")?.ifBlank { null }
val sonatypePassword: String? =
    (project.findProperty("SONATYPE_PASSWORD") as? String)?.ifBlank { null }
        ?: System.getenv("ORG_GRADLE_PROJECT_SONATYPE_PASSWORD")?.ifBlank { null }

// Maven Central requires a javadoc JAR for every publication. Use an empty
// JAR until Dokka is wired up as a quality improvement.
val emptyJavadocJar =
    project.tasks.register<Jar>("emptyJavadocJar") {
        archiveClassifier.set("javadoc")
    }

project.extensions.configure<org.gradle.api.publish.PublishingExtension> {
    repositories {
        maven {
            name = "sonatype"
            // NEW Central Portal OSSRH Staging API (legacy s01.oss.sonatype.org retired June 2025)
            url = uri("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/")
            credentials {
                username = sonatypeUsername ?: "unknown"
                password = sonatypePassword ?: ""
            }
        }
        mavenLocal()
    }
    publications.withType<org.gradle.api.publish.maven.MavenPublication>().configureEach {
        artifact(emptyJavadocJar)
        groupId = libraryGroup
        version = libraryVersion
        pom {
            name.set("$pomName - $artifactId")
            description.set(pomDescription)
            url.set(pomUrl)
            inceptionYear.set(pomInceptionYear)
            licenses {
                license {
                    name.set(pomLicenseName)
                    url.set(pomLicenseUrl)
                    distribution.set("repo")
                }
            }
            developers {
                developer {
                    id.set(pomDeveloperId)
                    name.set(pomDeveloperName)
                    if (pomDeveloperEmail.isNotBlank()) email.set(pomDeveloperEmail)
                    url.set(pomDeveloperUrl)
                }
            }
            scm {
                url.set(pomScmUrl)
                connection.set(pomScmConnection)
                developerConnection.set(pomScmDevConnection)
            }
        }
    }
}

// Register signing + fix sign/publish task ordering in afterEvaluate.
// (Mirrors playbook §5.2 verbatim. The Android-specific addition is the
// publications.create("release") block, because AGP's release component
// only exists post-evaluate.)
if (hasSigningKey) {
    if (hasSigningKeyFromContent) {
        project.extensions.configure<org.gradle.plugins.signing.SigningExtension> {
            val keyId = signingKeyId?.takeIf { it.isNotBlank() }
            if (keyId != null) {
                useInMemoryPgpKeys(keyId, signingKeyContent!!, signingPassword!!)
            } else {
                useInMemoryPgpKeys(signingKeyContent!!, signingPassword!!)
            }
        }
    }
    project.afterEvaluate {
        // ── Android-specific: create the release MavenPublication ──────────
        // KMP/JVM auto-register publications; Android does not. Create it
        // here BEFORE signing.sign(publications) is called so the live
        // collection includes it when the signing listener iterates.
        val publishingExt =
            project.extensions.getByType<org.gradle.api.publish.PublishingExtension>()
        val releaseComponent = project.components.findByName("release")
        if (releaseComponent != null && publishingExt.publications.findByName("release") == null) {
            publishingExt.publications.create(
                "release",
                org.gradle.api.publish.maven.MavenPublication::class.java,
            ).apply {
                from(releaseComponent)
            }
        }

        project.extensions.findByType<org.gradle.plugins.signing.SigningExtension>()?.let { signingExt ->
            signingExt.sign(
                project.extensions.getByType<org.gradle.api.publish.PublishingExtension>().publications,
            )
        }

        // Ensure publish tasks depend on all sign tasks — prevents ordering
        // failures when publications include extra artifacts (the empty
        // javadoc JAR).
        val signTasks =
            project.tasks.matching { it.name.startsWith("sign") && it.name.endsWith("Publication") }
        val publishToRepoTasks =
            project.tasks.matching {
                it.name.startsWith("publish") &&
                    it.name.contains("PublicationTo") &&
                    it.name.endsWith("Repository")
            }
        publishToRepoTasks.configureEach { dependsOn(signTasks) }

        project.logger.lifecycle(
            "[publish-convention] signing wired in ${project.path} " +
                "(mode=${if (hasSigningKeyFromContent) "in-memory" else "keyring-file"})",
        )
    }
} else {
    project.logger.warn(
        "[publish-convention] no signing key configured for ${project.path} — " +
            "uploads will be REJECTED by Maven Central. " +
            "Set signingInMemoryKey/Id/Password (CI) or signing.keyFile (local).",
    )
}

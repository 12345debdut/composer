/**
 * Precompiled script plugin for publishing Android library modules to Maven
 * Central via the Central Publisher Portal (ossrh-staging-api.central.sonatype.com).
 *
 * Supports two signing modes:
 *   - Mode A (local): binary keyring file via signing.keyFile / signing.secretKeyRingFile
 *   - Mode B (CI):    in-memory ASCII-armored key via signingInMemoryKey
 *
 * Apply via: apply(plugin = "publish-convention")
 *
 * The module's own build.gradle.kts must:
 *   - Apply the Android library plugin
 *   - Declare android { publishing { singleVariant("release") { withSourcesJar() } } }
 *
 * This convention plugin then registers the "release" MavenPublication, configures
 * its POM, hooks up the Sonatype repository, and wires signing.
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
// This is critical: subprojects often cannot see ~/.gradle/gradle.properties
// directly. Looking up via rootProject.findProperty() guarantees we find the
// values. Causes the cryptic "no configured signatory" error otherwise.
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

val signingKeyContent: String? =
    (root.findProperty("signingInMemoryKey") as? String)?.ifBlank { null }
        ?: System.getenv("ORG_GRADLE_PROJECT_signingInMemoryKey")?.ifBlank { null }

val hasSigningKeyFromFile =
    keyFileAbsolutePath != null && signingKeyId != null && signingPassword != null
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

    // Configure the signatory IMMEDIATELY (at script-evaluation time, not in
    // afterEvaluate). The signing plugin needs the signatory present before
    // any sign task is requested. Doing this in afterEvaluate is a known
    // source of "no signatures uploaded" because by then the publish task
    // graph snapshot has already been built.
    val signingExtEarly =
        project.extensions.getByType<org.gradle.plugins.signing.SigningExtension>()
    if (hasSigningKeyFromContent) {
        if (signingKeyId != null) {
            signingExtEarly.useInMemoryPgpKeys(signingKeyId, signingKeyContent!!, signingPassword!!)
        } else {
            signingExtEarly.useInMemoryPgpKeys(signingKeyContent!!, signingPassword!!)
        }
    }
}

// ── Sonatype credentials (Central Portal user token) ────────────────────────
val sonatypeUsername: String? =
    (project.findProperty("SONATYPE_USERNAME") as? String)?.ifBlank { null }
        ?: System.getenv("ORG_GRADLE_PROJECT_SONATYPE_USERNAME")?.ifBlank { null }
val sonatypePassword: String? =
    (project.findProperty("SONATYPE_PASSWORD") as? String)?.ifBlank { null }
        ?: System.getenv("ORG_GRADLE_PROJECT_SONATYPE_PASSWORD")?.ifBlank { null }

// Maven Central requires a javadoc JAR for every publication. We register an
// empty one — Dokka can replace it later as a quality improvement.
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
}

// ── Sign EVERY publication, current and future ──────────────────────────────
// Use publications.all { signingExt.sign(this) } at top level — this is the
// canonical Gradle pattern for reacting to current AND future publications.
// When AGP / our afterEvaluate block creates the "release" publication
// later, the closure fires and a Sign task is created bound to that exact
// publication BEFORE the publish task graph is finalized. This is the
// difference between .asc files being uploaded vs. Sonatype rejecting with
// "Missing signature for file".
if (hasSigningKey) {
    val signingExtLive =
        project.extensions.getByType<org.gradle.plugins.signing.SigningExtension>()
    val publishingLive =
        project.extensions.getByType<org.gradle.api.publish.PublishingExtension>()
    publishingLive.publications.all {
        signingExtLive.sign(this)
        project.logger.lifecycle(
            "[publish-convention] sign task wired for publication '${this.name}' in ${project.path}",
        )
    }
}

// Android library publications must be created AFTER AGP evaluates the
// project (the "release" software component only exists post-evaluate).
// Use eager `create` (not lazy `register`) so the publication is realized
// immediately and signing.sign(publication) can wire a Sign task to it
// in the same afterEvaluate pass — otherwise the Sign task is missing
// from the publish task graph and no .asc files are uploaded.
project.afterEvaluate {
    val publishing =
        project.extensions.getByType<org.gradle.api.publish.PublishingExtension>()

    val releaseComponent = project.components.findByName("release")
    val releasePublication: org.gradle.api.publish.maven.MavenPublication? =
        if (releaseComponent != null) {
            (publishing.publications.findByName("release") as? org.gradle.api.publish.maven.MavenPublication)
                ?: publishing.publications.create(
                    "release",
                    org.gradle.api.publish.maven.MavenPublication::class.java,
                ).apply { from(releaseComponent) }
        } else {
            null
        }

    publishing.publications.withType<org.gradle.api.publish.maven.MavenPublication>().configureEach {
        artifact(emptyJavadocJar)
        groupId = libraryGroup
        artifactId = project.name
        version = libraryVersion
        pom {
            name.set(pomName)
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

    // Belt-and-braces: ensure every publish-to-repository task depends on
    // every sign-publication task. Avoids "uses output of task without
    // declaring dependency" when extra artifacts (empty javadoc jar) are
    // attached lazily.
    if (hasSigningKey && releasePublication != null) {
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
            "[publish-convention] signing wired: project=${project.path}, " +
                "publication=release, mode=${if (hasSigningKeyFromContent) "in-memory" else "keyring-file"}",
        )
    } else if (!hasSigningKey) {
        project.logger.warn(
            "[publish-convention] no signing key configured for ${project.path} — " +
                "uploads will be REJECTED by Maven Central. " +
                "Set signingInMemoryKey/Id/Password (CI) or signing.keyFile (local).",
        )
    }
}

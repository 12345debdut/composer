plugins {
    `java-platform`
    `maven-publish`
    signing
}

val libraryGroup: String =
    project.findProperty("LIBRARY_GROUP") as? String ?: "io.github.12345debdut"
val libraryVersion: String =
    project.findProperty("LIBRARY_VERSION") as? String
        ?: project.findProperty("VERSION_NAME") as? String
        ?: "1.0.0"

group = libraryGroup
version = libraryVersion

javaPlatform {
    allowDependencies()
}

// Reproducible archives — keep BOM byte-identical across runs.
tasks.withType<AbstractArchiveTask>().configureEach {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}

dependencies {
    constraints {
        api("io.github.12345debdut:composer:$libraryVersion")
        api("io.github.12345debdut:composer-compose:$libraryVersion")
        api("io.github.12345debdut:composer-fragment:$libraryVersion")
    }
}

// ── Signing config — RESOLVED FROM ROOT PROJECT ─────────────────────────────
// Same lookup pattern as buildSrc/publish-convention.gradle.kts so the BOM
// behaves identically to the library modules.
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

if (hasSigningKeyFromFile) {
    project.extra["signing.keyId"] = signingKeyId!!
    project.extra["signing.password"] = signingPassword!!
    project.extra["signing.secretKeyRingFile"] = keyFileAbsolutePath!!
}

// ── Sonatype credentials ────────────────────────────────────────────────────
val sonatypeUsername: String? =
    (project.findProperty("SONATYPE_USERNAME") as? String)?.ifBlank { null }
        ?: System.getenv("ORG_GRADLE_PROJECT_SONATYPE_USERNAME")?.ifBlank { null }
val sonatypePassword: String? =
    (project.findProperty("SONATYPE_PASSWORD") as? String)?.ifBlank { null }
        ?: System.getenv("ORG_GRADLE_PROJECT_SONATYPE_PASSWORD")?.ifBlank { null }

publishing {
    repositories {
        maven {
            name = "sonatype"
            url = uri("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/")
            credentials {
                username = sonatypeUsername ?: "unknown"
                password = sonatypePassword ?: ""
            }
        }
        mavenLocal()
    }
    publications {
        create<MavenPublication>("bom") {
            from(components["javaPlatform"])
            groupId = libraryGroup
            artifactId = "composer-bom"
            version = libraryVersion
            pom {
                name.set("Composer BOM")
                description.set("Bill of Materials for the Composer state management SDK")
                url.set("https://github.com/12345debdut/composer")
                inceptionYear.set("2025")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("12345debdut")
                        name.set("Debdut Saha")
                        email.set("debdut.saha.1@gmail.com")
                        url.set("https://github.com/12345debdut")
                    }
                }
                scm {
                    url.set("https://github.com/12345debdut/composer")
                    connection.set("scm:git:git://github.com/12345debdut/composer.git")
                    developerConnection.set("scm:git:ssh://git@github.com/12345debdut/composer.git")
                }
            }
        }
    }
}

if (hasSigningKey) {
    signing {
        if (hasSigningKeyFromContent) {
            if (signingKeyId != null) {
                useInMemoryPgpKeys(signingKeyId, signingKeyContent!!, signingPassword!!)
            } else {
                useInMemoryPgpKeys(signingKeyContent!!, signingPassword!!)
            }
        }
        sign(publishing.publications["bom"])
    }
    logger.lifecycle(
        "[composer-bom] signing wired: publication=bom, mode=${if (hasSigningKeyFromContent) "in-memory" else "keyring-file"}",
    )
} else {
    logger.warn(
        "[composer-bom] no signing key configured — uploads will be REJECTED by Maven Central. " +
            "Set signingInMemoryKey/Id/Password (CI) or signing.keyFile (local).",
    )
}

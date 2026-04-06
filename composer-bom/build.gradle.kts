plugins {
    `java-platform`
    alias(libs.plugins.vanniktech.publish)
}

javaPlatform {
    allowDependencies()
}

dependencies {
    constraints {
        api("io.github.debdutsaha:composer:2.0.0")
        api("io.github.debdutsaha:composer-compose:2.0.0")
    }
}

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates("io.github.debdutsaha", "composer-bom", "2.0.0")

    pom {
        name.set("Composer BOM")
        description.set("Bill of Materials for the Composer state management SDK")
        url.set("https://github.com/12345debdut/composerlibrary")
        inceptionYear.set("2025")

        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("repo")
            }
        }

        developers {
            developer {
                id.set("debdutsaha")
                name.set("Debdut Saha")
                email.set("debdut.saha.1@gmail.com")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/12345debdut/composerlibrary.git")
            developerConnection.set("scm:git:ssh://github.com/12345debdut/composerlibrary.git")
            url.set("https://github.com/12345debdut/composerlibrary")
        }
    }
}

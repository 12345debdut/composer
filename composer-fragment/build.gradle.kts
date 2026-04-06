plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.vanniktech.publish)
    alias(libs.plugins.binary.compat)
}

android {
    namespace = "com.debdut.composer.fragment"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    kotlin {
        explicitApi()
    }
}

dependencies {
    api(project(":composer"))
    api(libs.androidx.fragment.ktx)
}

mavenPublishing {
    coordinates("io.github.debdutsaha", "composer-fragment", project.property("VERSION_NAME").toString())

    pom {
        name.set("Composer Fragment")
        description.set("Fragment integration for the Composer state management library")
        url.set("https://github.com/12345debdut/composerlibrary")

        licenses {
            license {
                name.set("Apache License 2.0")
                url.set("https://www.apache.org/licenses/LICENSE-2.0")
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
            url.set("https://github.com/12345debdut/composerlibrary")
            connection.set("scm:git:git://github.com/12345debdut/composerlibrary.git")
            developerConnection.set("scm:git:ssh://github.com/12345debdut/composerlibrary.git")
        }
    }

    signAllPublications()
}

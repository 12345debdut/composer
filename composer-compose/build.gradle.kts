plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.vanniktech.publish)
    alias(libs.plugins.binary.compat)
    alias(libs.plugins.dokka)
}

android {
    namespace = "com.debdut.composer.compose"
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
    buildFeatures {
        compose = true
    }
}

kotlin {
    explicitApi()
}

dependencies {
    api(project(":composer"))

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.runtime)
    implementation(libs.compose.ui)
    implementation(libs.lifecycle.runtime.compose)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates("io.github.debdutsaha", "composer-compose", project.property("VERSION_NAME").toString())

    pom {
        name.set("Composer Compose")
        description.set("Jetpack Compose extensions for the Composer state management SDK")
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

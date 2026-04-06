plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.vanniktech.publish)
    alias(libs.plugins.binary.compat)
}

android {
    namespace = "com.debdut.composer"
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
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(libs.androidx.core.ktx)
    api(libs.androidx.fragment.ktx)
    api(libs.androidx.lifecycle.viewmodel.ktx)
    api(libs.androidx.lifecycle.runtime.ktx)
    api(libs.androidx.lifecycle.livedata.ktx)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.turbine)
    testImplementation(libs.mockk)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates("io.github.debdutsaha", "composer", "2.0.0")

    pom {
        name.set("Composer")
        description.set("A state management SDK implementing unidirectional data flow for Android applications")
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
                url.set("https://github.com/12345debdut")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/12345debdut/composerlibrary.git")
            developerConnection.set("scm:git:ssh://github.com/12345debdut/composerlibrary.git")
            url.set("https://github.com/12345debdut/composerlibrary")
        }
    }
}

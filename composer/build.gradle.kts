import java.util.Properties

plugins {
    id("com.android.library")
    alias(libs.plugins.kotlin.android)
    `maven-publish`
}

android {
    namespace = "com.debdut.library.composer"
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

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

// Helper function to load properties from local.properties
fun getLocalProperty(key: String): String? {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        val properties = Properties()
        localPropertiesFile.inputStream().use { properties.load(it) }
        return properties.getProperty(key)?.trim()?.takeIf { it.isNotEmpty() }
    }
    return null
}

// Get GitHub repository info
fun getGitHubUser(): String {
    return getLocalProperty("GITHUB_USER") 
        ?: findProperty("GITHUB_USER") as String? 
        ?: System.getenv("GITHUB_USER")
        ?: throw IllegalStateException("GITHUB_USER is not set. Please set it in local.properties")
}

fun getGitHubRepo(): String {
    return getLocalProperty("GITHUB_REPO") 
        ?: findProperty("GITHUB_REPO") as String? 
        ?: System.getenv("GITHUB_REPO")
        ?: "composerlibrary" // Default repository name
}

// Publishing configuration for GitHub Packages
afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])
                
                groupId = "com.debdut.library"
                artifactId = "composer"
                version = getLocalProperty("VERSION_NAME") 
                    ?: findProperty("VERSION_NAME") as String? 
                    ?: System.getenv("VERSION_NAME") 
                    ?: "1.0.0"
                
                pom {
                    name.set("Composer Library")
                    description.set("A state management library that implements a unidirectional data flow architecture for Android applications")
                    url.set("https://github.com/${getGitHubUser()}/${getGitHubRepo()}")
                    
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    
                    developers {
                        developer {
                            id.set(getGitHubUser())
                            name.set(getLocalProperty("PACKAGE_DEVELOPER_NAME")?.trim()?.removeSurrounding("\"") ?: getGitHubUser())
                            email.set(getLocalProperty("PACKAGE_DEVELOPER_EMAIL")?.trim()?.removeSurrounding("\"") ?: "")
                        }
                    }
                    
                    scm {
                        val repo = getGitHubRepo()
                        val user = getGitHubUser()
                        connection.set("scm:git:git://github.com/${user}/${repo}.git")
                        developerConnection.set("scm:git:ssh://github.com/${user}/${repo}.git")
                        url.set("https://github.com/${user}/${repo}")
                    }
                }
            }
        }
        
        repositories {
            maven {
                name = "GitHubPackages"
                val user = getGitHubUser()
                val repo = getGitHubRepo()
                url = uri("https://maven.pkg.github.com/${user}/${repo}")
                credentials {
                    username = user
                    password = getLocalProperty("GITHUB_TOKEN") 
                        ?: findProperty("GITHUB_TOKEN") as String? 
                        ?: System.getenv("GITHUB_TOKEN")
                        ?: throw IllegalStateException("GITHUB_TOKEN is not set. Please set it in local.properties")
                }
            }
        }
    }
}
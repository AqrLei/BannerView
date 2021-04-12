plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    compileSdkVersion(App.compileSdkVersion)

    defaultConfig {
        minSdkVersion(App.minSdkVersion)
        targetSdkVersion(App.targetSdkVersion)
        versionCode = 1000400
        versionName = "1.0.4"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(fileTree("dir" to "libs", "include" to listOf("*.jar")))
    implementation(Libs.kotlin.stdlib)
    implementation(Libs.android.appcompat)
    implementation(Libs.android.viewPager2)
    implementation(Libs.android.constraintLayout)
}

apply(from = rootProject.file("gradle/maven_publish.gradle"))

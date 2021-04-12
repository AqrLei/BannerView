plugins {
    id("com.android.application")
    id("kotlin-android")
}

android {
    compileSdkVersion(App.compileSdkVersion)

    defaultConfig {
        applicationId("com.aqrlei.bannerview.sample")
        minSdkVersion(App.minSdkVersion)
        targetSdkVersion(App.targetSdkVersion)
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    implementation(Libs.android.core_ktx)
    implementation(Libs.android.appcompat)
    implementation(Libs.android.constraintLayout)
    implementation(Libs.android.viewPager2)
    implementation(Libs.android.material)
    testImplementation(Libs.junit)
    androidTestImplementation(Libs.android.juint_ext)

    androidTestImplementation(Libs.android.espresso)

    implementation(project(":bannerview"))

}
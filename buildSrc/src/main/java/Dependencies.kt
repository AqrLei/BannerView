import org.gradle.api.artifacts.dsl.RepositoryHandler

fun addRepos(handler: RepositoryHandler) {
    handler.google()
    handler.jcenter()
    handler.mavenCentral()
    handler.maven { setUrl("https://jitpack.io") }
}

object App {
    const val compileSdkVersion = 30
    const val minSdkVersion = 19
    const val targetSdkVersion = 30
}

object Versions {
    const val android_gradle_plugin = "4.1.1"
    const val appcompat = "1.2.0"
    const val core_ktx = "1.3.1"
    const val constraintLayout = "1.1.3"

    const val dokka_plugin = "0.10.1"

    const val espresso = "3.2.0"
    const val junit_ext = "1.1.1"

    const val kotlin = "1.4.21"
    const val kotlin_coroutine = "1.4.2"

    const val material = "1.2.1"
    const val viewpager2 = "1.0.0"
}

 object Android {
    const val plugin = "com.android.tools.build:gradle:${Versions.android_gradle_plugin}"
    const val core_ktx = "androidx.core:core-ktx:${Versions.core_ktx}"
    const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
    const val constraintLayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"

    const val viewPager2 = "androidx.viewpager2:viewpager2:${Versions.viewpager2}"
    const val material = "com.google.android.material:material:${Versions.material}"

    const val juint_ext = "androidx.test.ext:junit:${Versions.junit_ext}"
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"

}

object Kotlin {

    const val dokka_plugin = "org.jetbrains.dokka:dokka-gradle-plugin:${Versions.dokka_plugin}"
    const val plugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
    const val coroutine ="org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.kotlin_coroutine}"

}

object Libs {

    val android = Android

    const val junit = "junit:junit:4.12"

    val kotlin = Kotlin


}
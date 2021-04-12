// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    addRepos(repositories)
    dependencies {
        classpath(Libs.android.plugin)
        classpath(Libs.kotlin.plugin)
        classpath(Libs.kotlin.dokka_plugin)

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    addRepos(repositories)
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
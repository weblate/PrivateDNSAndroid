// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:9.3.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.2.10")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
plugins {
    id("org.jetbrains.kotlin.plugin.serialization") version "2.4.10"
    id("com.google.devtools.ksp") version "2.3.4" apply false
    id("com.mikepenz.aboutlibraries.plugin") version "15.0.4" apply false
    id("com.mikepenz.aboutlibraries.plugin.android") version "15.0.4" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.10" apply false
}

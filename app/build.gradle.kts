import com.android.build.api.dsl.ApplicationExtension

plugins {
    id("com.android.application")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.mikepenz.aboutlibraries.plugin")
    id("com.mikepenz.aboutlibraries.plugin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

extensions.configure<ApplicationExtension>  {
    compileSdk = 37
    androidResources {
        generateLocaleConfig = true
    }
    defaultConfig {
        applicationId = "ru.karasevm.privatednstoggle"
        versionCode = 19
        versionName = "1.11.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        targetSdk = 37
        minSdk = 28
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            manifestPlaceholders["appIcon"] = "@mipmap/ic_launcher"
        }
        debug {
            applicationIdSuffix = ".dev"
            manifestPlaceholders["appIcon"] = "@mipmap/ic_launcher_debug"
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    dependenciesInfo {
        // Disables dependency metadata when building APKs.
        includeInApk = false
        // Disables dependency metadata when building Android App Bundles.
        includeInBundle = false
    }
    namespace = "ru.karasevm.privatednstoggle"
}

dependencies {
    implementation("androidx.core:core-ktx:1.19.0")
    implementation("androidx.appcompat:appcompat:1.7.1")
    implementation("androidx.recyclerview:recyclerview:1.4.0")
    implementation("androidx.activity:activity-ktx:1.13.0")
    implementation("androidx.fragment:fragment-ktx:1.8.9")
    implementation("com.google.android.material:material:1.14.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    implementation("com.google.guava:guava:33.6.0-android")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.11.0")

    val shizukuVersion = "13.1.5"
    implementation("dev.rikka.shizuku:api:$shizukuVersion")
    implementation("dev.rikka.shizuku:provider:$shizukuVersion")
    compileOnly("dev.rikka.hidden:stub:4.4.0")

    implementation("org.lsposed.hiddenapibypass:hiddenapibypass:6.1")

    // Room components
    val roomVersion = "2.8.4"
    implementation("androidx.room:room-ktx:$roomVersion")
    ksp("androidx.room:room-compiler:$roomVersion")
    androidTestImplementation("androidx.room:room-testing:$roomVersion")

    // Lifecycle components
    val lifecycleVersion = "2.11.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycleVersion")

    // Compose
    val composeBom = platform("androidx.compose:compose-bom:2026.06.01")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.activity:activity-compose:1.13.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.compose.runtime:runtime-livedata:1.11.4")


    val latestAboutLibsRelease = "15.0.4"
    implementation("com.mikepenz:aboutlibraries-core:$latestAboutLibsRelease")
    implementation("com.mikepenz:aboutlibraries-compose-m3:${latestAboutLibsRelease}")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.7.0")
}
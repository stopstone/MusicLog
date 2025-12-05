import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
    id("androidx.navigation.safeargs.kotlin")
    id("kotlin-parcelize")
    alias(libs.plugins.google.gms.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

val localPropertiesFile = rootProject.file("local.properties")
val properties = Properties()
properties.load(FileInputStream(localPropertiesFile))

android {
    namespace = "com.stopstone.musicplaylist"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.stopstone.musicplaylist"
        minSdk = 24
        targetSdk = 35
        versionCode = 21
        versionName = "1.7.1"

        val clientId = properties["client.id"].toString().replace("\"", "")
        val clientSecret = properties["client.secret"].toString().replace("\"", "")
        val facebookAppId = properties["facebook.app.id"].toString().replace("\"", "")
        val kakaoNativeAppKey = properties["kakao.native.app.key"].toString().replace("\"", "")

        buildConfigField("String", "CLIENT_ID", "\"$clientId\"")
        buildConfigField("String", "CLIENT_SECRET", "\"$clientSecret\"")
        buildConfigField("String", "FACEBOOK_APP_ID", "\"$facebookAppId\"")
        buildConfigField("String", "KAKAO_NATIVE_APP_KEY", "\"$kakaoNativeAppKey\"")
        manifestPlaceholders["kakaoAppKey"] = kakaoNativeAppKey

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
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
    // Kakao Module
    implementation(libs.v2.all)

    // Splash
    implementation(libs.androidx.core.splashscreen)
    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // JetpackNavigation
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    // Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.extensions)
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.fragment.ktx)

    // Retrofit2
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // OkHttp3
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Glide
    implementation(libs.glide)

    // Room
    implementation(libs.androidx.room.runtime)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

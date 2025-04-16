plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.hilt)
    alias(libs.plugins.jetbrainsKotlinSerialization)
    id("kotlin-kapt")
}

sonar {
    properties {
        property("sonar.projectKey", "HERRERA99_TFG-AppTrackActividades")
        property("sonar.organization", "herrera99")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

android {
    namespace = "com.aitor.trackactividades"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.aitor.trackactividades"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    implementation(libs.coil)
    implementation(libs.location)
    implementation(libs.coroutines)

    // Hilt
    implementation(libs.hilt)
    kapt(libs.hilt.compiler)
    implementation(libs.hilt.navigation)

    // OSM
    implementation(libs.osmandroid)

    // Navigation
    implementation(libs.navigation.compose)
    implementation(libs.kotlin.serialization)

    // LiveData
    implementation(libs.runtime.livedata)

    // Google fonts
    implementation(libs.google.fonts)

    // DataStore
    implementation(libs.data.store)

    // Maps
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)

    // Corrutines Play Services
    implementation(libs.corrutines.play.services)

    // Mockito
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    androidTestImplementation(libs.mockito.android)

    // Gson
    implementation(libs.gson)

    // Paginacion
    implementation(libs.paging.compose)
    implementation(libs.paging.runtime)

    implementation(libs.lottie.compose)
    implementation(libs.material.icons)

    // Vico
    implementation(libs.vico.compose)
    implementation(libs.vico.compose.m3)
    implementation(libs.vico.views)

    // Refresh
    implementation(libs.swiperefresh)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
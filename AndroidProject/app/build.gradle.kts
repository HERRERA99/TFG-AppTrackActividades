plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.hilt)
    alias(libs.plugins.jetbrainsKotlinSerialization)
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("com.google.firebase.firebase-perf")
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
    packagingOptions {
        resources {
            excludes.add("META-INF/LICENSE.md")
            excludes.add("META-INF/LICENSE-notice.md")
            excludes.add("META-INF/NOTICE.md")
            excludes.add("META-INF/*.md")
            excludes.add("META-INF/versions/9/previous-compilation-data.bin")
        }
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

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)

    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")

    implementation("com.google.firebase:firebase-perf")

    implementation("com.google.firebase:firebase-messaging:23.4.1")

    androidTestImplementation("com.google.dagger:hilt-android-testing:2.51.1")
    kaptAndroidTest("com.google.dagger:hilt-compiler:2.51.1")

    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
    androidTestImplementation("io.mockk:mockk-android:1.13.8")

    // JUnit 5 API
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    // JUnit 5 Engine
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
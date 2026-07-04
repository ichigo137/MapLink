plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.maplink"

    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.maplink"
        minSdk = 27
        targetSdk = 36

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



    buildFeatures {
        compose = true
    }
}

dependencies {

    // -------------------------
    // Compose
    // -------------------------

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)

    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    testImplementation(libs.junit)

    // -------------------------
    // Firebase
    // -------------------------

    implementation(platform(libs.firebase.bom))

    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    implementation(libs.firebase.messaging)

    implementation("com.google.firebase:firebase-analytics")

    // -------------------------
    // Navigation
    // -------------------------

    implementation(libs.navigation.compose)

    // -------------------------
    // Location
    // -------------------------

    implementation(libs.play.services.location)

    // -------------------------
    // Lifecycle
    // -------------------------

    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.9.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.1")

    // -------------------------
    // Material Icons
    // -------------------------

    implementation("androidx.compose.material:material-icons-extended")

    // -------------------------
    // Coil
    // -------------------------

    implementation("io.coil-kt:coil-compose:2.7.0")

    // -------------------------
    // Coroutines
    // -------------------------

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")

    // -------------------------
    // MapLibre
    // -------------------------

    implementation("org.maplibre.gl:android-sdk:11.13.0")

    implementation("com.google.accompanist:accompanist-permissions:0.37.3")

}
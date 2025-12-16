import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// Load local.properties (Kotlin DSL)
val localProps = Properties().apply {
    val lpFile = rootProject.file("local.properties")
    if (lpFile.exists()) {
        lpFile.inputStream().use { load(it) }
    }
}

// Read key name from local.properties. Example key name: googleMapsApiKey=YOUR_KEY
val mapsKey: String = localProps.getProperty("googleMapsApiKey") ?: ""

android {
    namespace = "com.betanooblabs.googlemapsimplementation"
    compileSdk {
        version = release(36)
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }

    defaultConfig {
        applicationId = "com.betanooblabs.googlemapsimplementation"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        manifestPlaceholders["googleMapsApiKey"] = mapsKey

        buildConfigField(
            "String",
            "Google_Maps_API_Key",
            "\"$mapsKey\""
        )
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation(libs.play.services.maps)
    // Jetpack Compose bindings for the Maps SDK
    implementation(libs.maps.compose)
    // Places and Maps SDKs
    implementation("com.google.android.libraries.places:places:3.5.0")
}
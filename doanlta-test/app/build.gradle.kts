plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.services) // Google Sign-In
}

android {
    namespace = "com.example.dnhchongili"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.dnhchongili"
        minSdk = 24
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
}

dependencies {
    dependencies {
        // Room
        implementation(libs.room.runtime)
        annotationProcessor(libs.room.compiler)

        // RecyclerView
        implementation(libs.recyclerview)

        // Lifecycle
        implementation(libs.lifecycle.extensions)

        // Material Components
        implementation(libs.material)

        // Google Sign-In
        implementation(libs.play.services.auth)

        // Firebase Auth (nếu cần)
        implementation(libs.firebase.auth)

        // Glide
        implementation(libs.glide)
        annotationProcessor(libs.glide.compiler)

        // Gson
        implementation(libs.gson)

        // Encrypted SharedPreferences
        implementation(libs.security.crypto)

        // OkHttp for REST API (nếu dùng)
        implementation(libs.okhttp)

        // AndroidX Fragment
        implementation("androidx.fragment:fragment:1.6.2")

        // AppCompat & ConstraintLayout
        implementation(libs.appcompat)
        implementation(libs.constraintlayout)

        // Kotlin Stdlib
        implementation(libs.kotlin.stdlib)

        // =========================
        // Socket.IO client cho Android (chỉ cần 1 lần duy nhất)
        implementation("io.socket:socket.io-client:2.1.0") {
            exclude(group = "org.json", module = "json")
        }
        implementation("org.json:json:20200518")
        // =========================

        // Testing
        testImplementation(libs.junit)
        androidTestImplementation(libs.ext.junit)
        androidTestImplementation(libs.espresso.core)
    }
}

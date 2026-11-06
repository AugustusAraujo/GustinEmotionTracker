plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.example.gustinemotiontracker.core"
    compileSdk = 37

    defaultConfig {
        minSdk = 30
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    api(libs.androidx.room.runtime)
    api(libs.play.services.wearable)

    testImplementation(libs.junit)
}

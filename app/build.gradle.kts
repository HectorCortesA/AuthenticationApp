plugins {
    id("com.google.gms.google-services")
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

}

android {
    namespace = "com.hectorcortes.authhector"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.hectorcortes.authhector"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation ("com.facebook.android:facebook-android-sdk:16.0.0")

    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation("com.google.firebase:firebase-auth:23.0.0")

    implementation("com.google.android.gms:play-services-auth:21.2.0")

    implementation ("com.google.firebase:firebase-firestore:24.4.1")

    implementation("androidx.recyclerview:recyclerview:1.3.2")

    implementation("androidx.recyclerview:recyclerview-selection:1.1.0")
    implementation ("com.squareup.picasso:picasso:2.8")

    implementation ("com.github.bumptech.glide:glide:4.12.0")
    implementation ("com.github.bumptech.glide:annotations:4.12.0")


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.play.services.measurement.api)
    implementation(libs.firebase.storage.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
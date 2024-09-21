plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.kotlin.sacalabici"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kotlin.sacalabici"
        minSdk = 23
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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    //Fragment
    implementation ("androidx.fragment:fragment-ktx:1.5.0")
    //Activity
    implementation ("androidx.activity:activity-ktx:1.5.0")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    implementation("jp.wasabeef:glide-transformations:4.3.0")

    //Corrutinas
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")

    //Fragment
    implementation("androidx.fragment:fragment-ktx:1.5.0")
    //Activity
    implementation("androidx.activity:activity-ktx:1.5.0")
    //Data binding
    implementation("androidx.databinding:databinding-runtime:7.1.2")

    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation("com.google.android.gms:play-services-auth:20.5.0")

    implementation("androidx.compose.material3:material3:1.1.1")

    implementation("com.facebook.android:facebook-login:latest-version")
    implementation("com.facebook.android:facebook-android-sdk:latest.release")
}
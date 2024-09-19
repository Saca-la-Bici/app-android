plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id ("kotlin-android")
    id ("kotlin-parcelize")
}

android {
    namespace = "com.kotlin.sacalabici"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.kotlin.sacalabici"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Acceder al valor de MAPBOX_DOWNLOADS_TOKEN desde gradle.properties
        val mapboxToken: String = project.findProperty("MAPBOX_DOWNLOADS_TOKEN") as String? ?: ""

        // Agregarlo como un buildConfigField para usarlo en el código Kotlin
        buildConfigField("String", "MAPBOX_ACCESS_TOKEN", "\"${mapboxToken}\"")

        // Agregarlo como un recurso de string para usarlo en el AndroidManifest.xml
        resValue("string", "mapbox_access_token", "\"${mapboxToken}\"")
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

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.navigation.fragment)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //MAPBOX DEPENDENCY
    implementation(libs.maps.android)
    implementation(libs.maps.compose)

    //Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1")
    //Fragment
    implementation("androidx.fragment:fragment-ktx:1.5.0")
    //Data Binding
    implementation("androidx.databinding:databinding-runtime:7.1.2")
    //Activity
    implementation("androidx.activity:activity-ktx:1.5.0")
    //ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    //LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")

}
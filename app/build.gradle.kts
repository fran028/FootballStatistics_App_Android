plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    id("kotlin-kapt")
}


android {
    namespace = "com.example.footballstatistics_app_android"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.footballstatisticsapp"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true"
                )
            }
        }
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
    implementation(libs.firebase.firestore.ktx)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.androidx.tools.core)
    implementation(libs.androidx.runtime.livedata)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    implementation("androidx.compose.ui:ui-text-google-fonts:1.7.8")

    val roomversion = "2.6.1"

    implementation("androidx.room:room-runtime:$roomversion")
    annotationProcessor("androidx.room:room-compiler:$roomversion")
    kapt("androidx.room:room-compiler:$roomversion")
    implementation("androidx.room:room-ktx:$roomversion")
    implementation("androidx.room:room-rxjava2:$roomversion")
    implementation("androidx.room:room-rxjava3:$roomversion")
    implementation("androidx.room:room-guava:$roomversion")
    testImplementation("androidx.room:room-testing:$roomversion")
    implementation("androidx.room:room-paging:$roomversion")

    implementation("com.google.accompanist:accompanist-systemuicontroller:0.34.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    implementation("androidx.compose.material:material-icons-extended:1.7.8")

//    implementation("com.github.madrapps:plot:1.0.3")
    implementation("com.github.madrapps:plot:0.1.1")

    implementation("com.google.android.gms:play-services-wearable:19.0.0")
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("com.opencsv:opencsv:5.8")

}
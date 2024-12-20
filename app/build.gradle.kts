import com.android.build.api.dsl.VariantDimension

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
//    id("kotlin-kapt")
//    id("com.google.dagger.hilt.android")
//    kotlin("kapt")
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.devtool.ksp)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.example.zho.samplenewsapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.zho.samplenewsapp"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val buildConfigConstants = mapOf(
            "API_BASEURL" to "\"https://api.nytimes.com\"",
            "API_KEY" to "\"pf6FgeMTQXi38BAFb9voVvHtrEQlwUlp\"",
            "IMAGE_BASEURL" to "\"https://static01.nyt.com/\"",
        )
        addBuildConfigs(buildConfigConstants)
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isDebuggable = true
        }

        release {
            isMinifyEnabled = true
            isDebuggable = false
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
        buildConfig = true
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

//    implementation(libs.androidx.material3.icons.)
    implementation(libs.lifecycle.viewmodel.compose)

    implementation(libs.navigation.compose)
    implementation(libs.kotlin.serialization)

    implementation(libs.timber)

    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)

    implementation(libs.retrofit)
    implementation(libs.retrofit.convertergson)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)

    implementation(libs.room.runtime)
    ksp(libs.room.compiler)
    annotationProcessor(libs.room.compiler)
    implementation(libs.room.ktx)
    implementation(libs.room.paging)

    implementation(libs.hilt.android.lib)
    ksp(libs.hilt.android.compiler)
    implementation(libs.hilt.navigation.compose)

    implementation(libs.androidx.paging.runtime)
    testImplementation(libs.androidx.paging.common)
    implementation(libs.androidx.paging.compose)
}

fun VariantDimension.addBuildConfigs(constants: Map<String, String>) {
    constants.forEach { (key, value) ->
        buildConfigField("String", key, value)
    }
}

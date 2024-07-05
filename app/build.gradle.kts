plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.jetbrains.kotlin.kapt)
    id("kotlin-kapt")
}

android {
    namespace = "de.kontranik.freebudget"
    compileSdk = 34

    defaultConfig {
        vectorDrawables.useSupportLibrary = true
        applicationId = "com.kontranik.freebudget"
        minSdk = 21
        targetSdk = 34
        versionCode = 9
        versionName = "1.2.4"

        setProperty("archivesBaseName", applicationId + "-v" + versionCode + "(" + versionName + ")")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Room components
    implementation( libs.androidx.room.runtime)
    implementation( libs.androidx.room.ktx)
    kapt( libs.androidx.room.compiler)
    androidTestImplementation( libs.androidx.room.testing)

    // Lifecycle components
    implementation( libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.common.java8)

    implementation( libs.androidx.appcompat)
    implementation("com.google.android.material:material:1.7.0") // legacy material
    implementation( libs.androidx.recyclerview)
    // For control over item selection of both touch and mouse driven selection
    implementation( libs.androidx.recyclerview.selection)
    implementation( libs.androidx.constraintlayout)
    implementation( libs.androidx.legacy.support.v4)
    implementation( libs.androidx.preference.ktx)

    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.runtime.livedata)
    implementation(libs.androidx.compose.ui.tooling)
    implementation( libs.compose.theme.adapter)

    testImplementation( libs.junit)
    androidTestImplementation( libs.androidx.junit.v114)
    androidTestImplementation( libs.androidx.espresso.core.v350)

    implementation( libs.androidx.core.ktx.v190)
    implementation( libs.kotlin.stdlib.jdk7)
}


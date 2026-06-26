plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.perfumevault"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.perfumevault"
        minSdk = 24
        targetSdk = 35
        versionCode = 14
        versionName = "9.2"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions += "version"
    productFlavors {
        create("public") { // Umbenannt von "free" zu "public"
            dimension = "version"
            applicationId = "com.perfumevault"
            manifestPlaceholders["showAds"] = "true"
        }
        create("private") {
            dimension = "version"
            applicationIdSuffix = ".private"
            manifestPlaceholders["showAds"] = "false"
        }
    }

    buildTypes {
        release {
            signingConfig = signingConfigs.getByName("debug")

            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation("androidx.compose.animation:animation")
    implementation("androidx.compose.animation:animation-graphics")
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.fragment)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.coil.compose)
    implementation(libs.play.services.ads)
    implementation("com.android.billingclient:billing-ktx:7.1.1")

    debugImplementation(libs.androidx.compose.ui.tooling)
}

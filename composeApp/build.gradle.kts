import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
}

room {
    schemaDirectory("$projectDir/schemas")
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
        }
    }
    
    @OptIn(org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl::class)
    wasmJs {
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material3)
                implementation(compose.ui)
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                
                // KMP-compatible Lifecycle
                implementation("org.jetbrains.androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")
                implementation("org.jetbrains.androidx.lifecycle:lifecycle-runtime-compose:2.8.4")
                
                // Coil 3 for Multiplatform
                implementation("io.coil-kt.coil3:coil-compose:3.0.0-alpha10")
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.room.runtime)
                implementation(libs.androidx.room.ktx)
                implementation(libs.play.services.ads)
                implementation("com.android.billingclient:billing-ktx:7.1.1")
                implementation(libs.androidx.work.runtime.ktx)
            }
        }
        val wasmJsMain by getting {
            dependencies {
            }
        }
    }
}

android {
    namespace = "com.perfumevault"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.perfumevault.kmp"
        minSdk = 24
        targetSdk = 35
        versionCode = 14
        versionName = "9.2"
    }
    
    flavorDimensions += "version"
    productFlavors {
        create("public") {
            dimension = "version"
            applicationId = "com.perfumevault.kmp"
        }
        create("private") {
            dimension = "version"
            applicationIdSuffix = ".private"
        }
    }
    
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.game.wildcatcher"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.game.wildcatcher"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        // Support ASTC texture compression and high-res tablet graphics
        ndk {
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.11"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    // Room DB
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    ksp(libs.room.compiler)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    debugImplementation(libs.androidx.ui.tooling)
}

// Automatically deploy generated APK to Google Drive kids directory
tasks.register("deployApkToGoogleDrive") {
    group = "distribution"
    description = "Copies generated APK to G:/我的雲端硬碟/kids"
    doLast {
        val targetDir = File("G:/我的雲端硬碟/kids")
        if (targetDir.exists()) {
            val apkDir = File(layout.buildDirectory.asFile.get(), "outputs/apk")
            if (apkDir.exists()) {
                apkDir.walkTopDown().filter { it.extension == "apk" }.forEach { apkFile ->
                    val destFile = File(targetDir, apkFile.name)
                    apkFile.copyTo(destFile, overwrite = true)
                    println(">> [Google Drive Sync] Deployed ${apkFile.name} -> ${destFile.absolutePath}")
                }
            }
        }
    }
}

afterEvaluate {
    tasks.findByName("assembleDebug")?.finalizedBy("deployApkToGoogleDrive")
    tasks.findByName("assembleRelease")?.finalizedBy("deployApkToGoogleDrive")
}

plugins {
    alias(custom.plugins.androidApplication)
    alias(custom.plugins.composeCompiler)
}

android {
    namespace = "cc.worldmandia.composeexample"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "cc.worldmandia.composeexample"
        minSdk = 36
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isDebuggable = true
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
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":config-editor"))
    implementation(custom.androidx.activity.compose)
    testImplementation(custom.junit)
    androidTestImplementation(custom.androidx.junit)
    androidTestImplementation(custom.androidx.espresso.core)
}
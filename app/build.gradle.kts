plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.hamidzar2002.untangle"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.hamidzar2002.untangle"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "0.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        manifestPlaceholders["admobAppId"] = "ca-app-pub-6961751302262101~7558692283"
        buildConfigField(
            "String",
            "ADMOB_APP_ID",
            "\"ca-app-pub-6961751302262101~7558692283\""
        )
        buildConfigField(
            "String",
            "ADMOB_BANNER_ID",
            "\"ca-app-pub-6961751302262101/5961559645\""
        )
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    buildTypes {
        debug {
            // Google sample IDs ensure local development never requests live ads.
            manifestPlaceholders["admobAppId"] =
                "ca-app-pub-3940256099942544~3347511713"
            buildConfigField(
                "String",
                "ADMOB_APP_ID",
                "\"ca-app-pub-3940256099942544~3347511713\""
            )
            buildConfigField(
                "String",
                "ADMOB_BANNER_ID",
                "\"ca-app-pub-3940256099942544/9214589741\""
            )
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Werror")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.google.mobile.ads)
    implementation(libs.google.ump)

    debugImplementation(libs.androidx.compose.ui.tooling)
    testImplementation(libs.junit)
}

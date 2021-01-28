import java.util.*

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

android {
    signingConfigs {
        create("testKey") {
            keyAlias = "key0"
            keyPassword = "123456"
            storeFile = file("../testkey.jks")
            storePassword = "123456"
        }
    }

    buildToolsVersion = "30.0.3"
    compileSdkVersion(30)

    defaultConfig {
        applicationId = "net.inferno.quakereport"

        minSdkVersion(21)
        targetSdkVersion(30)

        versionCode = 1
        versionName = "1.0"

        vectorDrawables.useSupportLibrary = true
        buildFeatures {
            compose = true
        }

        testInstrumentationRunner("android.support.test.runner.AndroidJUnitRunner")
    }

    flavorDimensions("dev")

    productFlavors {
        maybeCreate("dev").apply {
            dimension = "dev"

            minSdkVersion(28)

            versionNameSuffix = "-dev" + "-" + Date().format("dd-MM-yyyy")

            buildConfigField("boolean", "DEV", "true")
        }
        maybeCreate("deploy").apply {
            dimension = "dev"

            minSdkVersion(21)

            buildConfigField("boolean", "DEV", "false")
        }
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
        }

        maybeCreate("preRelease").apply {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = true
            versionNameSuffix = "-" + Date().format("dd-MM-yyyy")

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs["testKey"]
        }

        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs["testKey"]
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
        freeCompilerArgs = freeCompilerArgs + arrayOf(
            "-Xallow-jvm-ir-dependencies",
            "-Xskip-prerelease-check",
            "-Xopt-in=kotlin.RequiresOptIn"
        )
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Versions.compose
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlin}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coRoutines}")

    //region AndroidX
    implementation("androidx.core:core-ktx:${Versions.core}")
    implementation("androidx.appcompat:appcompat:${Versions.appCompat}")
    implementation("androidx.preference:preference-ktx:${Versions.preferences}")
    implementation("androidx.fragment:fragment-ktx:${Versions.fragment}")
    implementation("androidx.activity:activity-ktx:${Versions.activity}")

    implementation("androidx.browser:browser:${Versions.browser}")
    //endregion

    //region UI Components
    implementation("androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}")
    implementation("androidx.recyclerview:recyclerview:${Versions.recyclerView}")
    implementation("com.google.android.material:material:${Versions.material}")
    //endregion

    //region Lifecycle Components
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}")
    //endregion

    //region Paging
    implementation("androidx.paging:paging-runtime:${Versions.pagingComponent}")
    //endregion

    //region Google
    implementation("com.google.android.gms:play-services-location:${Versions.googleLocationServices}")
    //endregion

    //region Compose
    implementation("androidx.compose.ui:ui:${Versions.compose}")
    implementation("androidx.compose.material:material:${Versions.compose}")
    implementation("androidx.compose.ui:ui-tooling:${Versions.compose}")
    implementation("androidx.compose.runtime:runtime-livedata:${Versions.compose}")

    implementation("androidx.navigation:navigation-compose:1.0.0-alpha06")
    implementation("androidx.paging:paging-compose:1.0.0-alpha06")
    //endregion

    //region Networking
    implementation("com.squareup.okhttp3:okhttp:${Versions.okHttp}")

    implementation("com.squareup.retrofit2:retrofit:${Versions.retrofit}")
    implementation("com.squareup.retrofit2:converter-moshi:${Versions.retrofit}")

    implementation("com.squareup.moshi:moshi-kotlin:${Versions.moshi}")
    kapt("com.squareup.moshi:moshi-kotlin-codegen:${Versions.moshi}")
    //endregion
}
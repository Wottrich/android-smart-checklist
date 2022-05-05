//plugins {
//    id 'com.android.library'
//    id 'kotlin-android'
//    id 'kotlin-android-extensions'
//    id 'kotlin-kapt'
//}

plugins {
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
    id(Plugins.kotlinAndroidExtensions)
    id(Plugins.kotlinKapt)
}

android {
    compileSdk = AndroidSdk.targetSdk
    buildToolsVersion = AndroidSdk.buildToolsVersion

    defaultConfig {
        minSdk = AndroidSdk.minSdk
        targetSdk = AndroidSdk.targetSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isMinifyEnabled = true
        }
    }
    compileOptions {
        sourceCompatibility = AndroidSdk.javaVersion
        targetCompatibility = AndroidSdk.javaVersion
    }
    kotlinOptions {
        jvmTarget = AndroidSdk.javaVersion.toString()
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeVersion
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    //Kotlin
    implementation(Libs.kotlinStdlib)
    implementation(Libs.androidCoreKtx)
    implementation(Libs.appCompat)
    implementation(Libs.androidMaterial)

    implementation(Libs.coroutinesLib)

    koin()

    //Android
//    api("androidx.lifecycle:lifecycle-extensions:$lifecycle_extensions_version")
//    api("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_viewmodel_version")
//    api("androidx.lifecycle:lifecycle-livedata-ktx:$livedata_lifecycle_version")

    unitTest()

}
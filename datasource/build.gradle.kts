plugins {
    id(Plugins.androidLibrary)
    id(Plugins.kotlinAndroid)
    id(Plugins.kotlinParcelize)
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
    }
    compileOptions {
        sourceCompatibility = AndroidSdk.javaVersion
        targetCompatibility = AndroidSdk.javaVersion
    }
    kotlinOptions {
        jvmTarget = AndroidSdk.javaVersion.toString()
    }
    namespace = "wottrich.github.io.datasource"
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    kotlinAndCoreKtx()

    implementation(project(path = ":infrastructure:generator:uuid"))

    //Database
    room(withCompiler = true)

    koin()

    //Test
    unitTest()

    //Test Instrumental
    instrumentalTest()

}
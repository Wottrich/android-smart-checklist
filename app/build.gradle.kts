//plugins {
//    id 'com.android.application'
//    id 'kotlin-android'
//    id 'kotlin-android-extensions'
//    id 'kotlin-kapt'
//}

plugins {
    id(Plugins.androidApplication)
    id(Plugins.kotlinAndroid)
    id(Plugins.kotlinKapt)
}

android {
    compileSdk = AndroidSdk.targetSdk
    buildToolsVersion = AndroidSdk.buildToolsVersion

    defaultConfig {
        applicationId = "wottrich.github.io.androidsmartchecklist"
        minSdk = AndroidSdk.minSdk
        targetSdk = AndroidSdk.targetSdk
        versionCode = 5
        versionName = "1.3.0"
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
            }
        }
    }

    buildTypes {
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            applicationIdSuffix = ".debug"
            isDebuggable = true
            versionNameSuffix = "-debug"
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
        kotlinCompilerVersion = Versions.kotlinVersion
    }

    packagingOptions {
        resources.excludes.apply {
            add("META-INF/DEPENDENCIES")
            add("META-INF/NOTICE")
            add("META-INF/LICENSE")
            add("META-INF/LICENSE.txt")
            add("META-INF/NOTICE.txt")
        }
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    kotlinAndCoreKtx()

    //modules
    implementation(project(path = ":datasource"))
    implementation(project(path = ":tools"))
    implementation(project(path = ":baseui"))
    implementation(project(path = ":features:checklist:impl"))
    implementation(project(path = ":features:task:impl"))
    implementation(project(path = ":features:newchecklist:impl"))
    implementation(project(path = ":features:newchecklist:public"))
    implementation(project(path = ":ui-aboutus"))
    implementation(project(path = ":ui-support"))
    moduleCommonUiCompose()

    composeUi()

    koin()

    navigation(withAnimation = true)

    implementation(Libs.appCompat)
    implementation(Libs.systemUiControllerAccompanist)

    //Test
    testImplementation(project(path = ":test-tools"))
    unitTest()

    //Test instrumental
    instrumentalTest()

}
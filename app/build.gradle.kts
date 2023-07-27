import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

plugins {
    id(Plugins.androidApplication)
    id(Plugins.kotlinAndroid)
    id(Plugins.kspPlugin)
}

android {
    compileSdk = AndroidSdk.targetSdk
    buildToolsVersion = AndroidSdk.buildToolsVersion

    defaultConfig {
        applicationId = "wottrich.github.io.androidsmartchecklist"
        minSdk = AndroidSdk.minSdk
        targetSdk = AndroidSdk.targetSdk
        versionCode = 7
        versionName = "1.5.0"
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
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
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
    namespace = "wottrich.github.io.androidsmartchecklist"
}

tasks.withType<JavaCompile> {
    sourceCompatibility = AndroidSdk.javaVersion.toString()
    targetCompatibility = AndroidSdk.javaVersion.toString()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = AndroidSdk.javaVersion.toString()
    }
}

kotlinExtension.jvmToolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.kotlin.stdlib)
    implementation(libs.android.core.ktx)
    implementation(libs.android.app.compat)
    implementation(libs.compose.accompanist.system.ui.controller)
    implementation(libs.bundles.compose.default)
    implementation(libs.bundles.koin.default)
    implementation(libs.bundles.compose.navigation.default)

    // Base Modules
    implementation(project(path = ":baseui"))
    implementation(project(path = ":datasource"))

    // Infrastructure
    implementation(project(path = ":infrastructure:extensions:date"))
    implementation(project(path = ":infrastructure:extensions:intent"))
    implementation(project(path = ":infrastructure:components:android"))
    implementation(project(path = ":infrastructure:components:kotlin"))
    implementation(project(path = ":common-ui-compose"))
    implementation(project(path = ":domain:coroutines"))

    // Features
    implementation(project(path = ":features:checklist:impl"))
    implementation(project(path = ":features:task:impl"))
    implementation(project(path = ":features:newchecklist:impl"))
    implementation(project(path = ":features:quicklychecklist:impl"))
    implementation(project(path = ":ui-aboutus"))
    implementation(project(path = ":ui-support"))

    //Test
    testImplementation(project(path = ":test-tools"))
    testImplementation(libs.bundles.test.default)
}
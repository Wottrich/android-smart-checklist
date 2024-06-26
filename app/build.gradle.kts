plugins {
    id("wottrich.github.io.smartchecklist.android.app")
    alias(libs.plugins.ksp)
}

android {
    defaultConfig {
        applicationId = "wottrich.github.io.androidsmartchecklist"
        versionCode = 9
        versionName = "2.0.1"
        multiDexEnabled = true

        buildConfigField("String", "PRIVACY_POLICY_URL", "\"https://github.com/Wottrich/android-smart-checklist/blob/master/privacity_rules.txt\"")
    }

    buildTypes {
        getByName("release") {
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    packaging {
        resources.excludes.apply {
            add("META-INF/DEPENDENCIES")
            add("META-INF/NOTICE")
            add("META-INF/LICENSE")
            add("META-INF/LICENSE.txt")
            add("META-INF/NOTICE.txt")
        }
    }
    namespace = "wottrich.github.io.smartchecklist"
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
    implementation(project(path = ":baseui"))
    implementation(project(path = ":datasource"))
    implementation(project(path = ":infrastructure:extensions:intent"))
    implementation(project(path = ":infrastructure:components:android"))
    implementation(project(path = ":infrastructure:components:kotlin"))
    implementation(project(path = ":domain:coroutines"))
    implementation(project(path = ":features:checklist:impl"))
    implementation(project(path = ":features:task:impl"))
    implementation(project(path = ":features:newchecklist:impl"))
    implementation(project(path = ":features:quicklychecklist:impl"))
    implementation(project(path = ":ui-privacy-policy:impl"))
    implementation(project(path = ":ui-aboutus"))
    implementation(project(path = ":ui-support"))
    testImplementation(project(path = ":test-tools"))
    testImplementation(libs.bundles.test.default)
}
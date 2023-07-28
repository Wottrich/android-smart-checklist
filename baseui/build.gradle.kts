plugins {
    id("wottrich.github.io.smartchecklist.android.lib")
}

android {
    buildFeatures.compose = true
    composeOptions.kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    namespace = "wottrich.github.io.baseui"
}

dependencies {
    implementation(libs.android.material)
    implementation(libs.bundles.compose.default)
    implementation(libs.bundles.compose.navigation.default)
    implementation(libs.bundles.koin.default)
    testImplementation(libs.bundles.test.default)
}
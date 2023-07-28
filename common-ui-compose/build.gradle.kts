plugins {
    id("wottrich.github.io.smartchecklist.android.lib")
}

android {
    buildFeatures.compose = true
    composeOptions.kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    namespace = "github.io.wottrich.common.ui.compose"
}

dependencies {
    implementation(libs.bundles.compose.default)
    implementation(project(path = ":baseui"))
    testImplementation(libs.bundles.test.default)
}
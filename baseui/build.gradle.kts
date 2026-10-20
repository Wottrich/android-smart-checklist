plugins {
    id("wottrich.github.io.smartchecklist.android.lib")
    id("wottrich.github.io.smartchecklist.compose")
}

android {
    namespace = "wottrich.github.io.smartchecklist.baseui"
}

dependencies {
    implementation(libs.android.material)
    implementation(libs.bundles.compose.default)
    implementation(libs.bundles.compose.navigation.default)
    implementation(libs.bundles.koin.default)
    testImplementation(libs.bundles.test.default)
}
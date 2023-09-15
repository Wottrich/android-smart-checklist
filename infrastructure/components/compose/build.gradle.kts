plugins {
    id("wottrich.github.io.smartchecklist.android.lib")
}

android {
    namespace = "wottrich.github.io.smartchecklist.compose"
}

dependencies {
    implementation(libs.bundles.compose.default)
    implementation(libs.bundles.compose.navigation.default)
}
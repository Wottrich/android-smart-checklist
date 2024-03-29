plugins {
    id("wottrich.github.io.smartchecklist.android.lib")
}

android {
    namespace = "wottrich.github.io.smartchecklist.android"
}

dependencies {
    implementation(libs.android.app.compat)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.coroutines.core)
    implementation(libs.bundles.koin.default)
    implementation(libs.compose.navigation)
    implementation(libs.compose.accompanist.navigation)
    implementation(project(":domain:coroutines"))
}
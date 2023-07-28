plugins {
    id("wottrich.github.io.smartchecklist.android.lib")
}

android {
    namespace = "github.io.wottrich.android"
}

dependencies {
    implementation(libs.android.app.compat)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.coroutines.core)
    implementation(libs.bundles.koin.default)
    implementation(project(":domain:coroutines"))
}
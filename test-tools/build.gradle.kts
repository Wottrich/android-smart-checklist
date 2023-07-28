plugins {
    id("wottrich.github.io.smartchecklist.android.lib")
}

android {
    namespace = "github.io.wottrich.test.tools"
}

dependencies {
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.test)
    implementation(libs.bundles.koin.default)
    implementation(libs.bundles.test.default)
    implementation(project(path = ":domain:coroutines"))
}
plugins {
    id("wottrich.github.io.smartchecklist.android.lib")
}

android {
    namespace = "wottrich.github.io.smartchecklist.testtools"
}

dependencies {
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.test)
    implementation(libs.bundles.koin.default)
    implementation(libs.bundles.test.default)
    implementation(project(path = ":domain:coroutines"))
}
plugins {
    id("wottrich.github.io.smartchecklist.feature.impl")
}

android {
    namespace = "github.io.wottrich.ui.aboutus"
}

dependencies {
    implementation(libs.bundles.compose.default)
    implementation(libs.bundles.koin.default)
    implementation(project(path = ":datasource"))
    implementation(project(path = ":baseui"))
    testImplementation(project(path = ":test-tools"))
    testImplementation(libs.bundles.test.default)
}
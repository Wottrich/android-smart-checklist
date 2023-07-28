plugins {
    id("wottrich.github.io.smartchecklist.feature.impl")
}

android {
    namespace = "wottrich.github.io.smartchecklist.uisupport"
}

dependencies {
    implementation(libs.bundles.compose.default)
    implementation(libs.bundles.compose.navigation.default)
    implementation(libs.bundles.koin.default)
    implementation(project(path = ":domain:coroutines"))
    implementation(project(path = ":datasource"))
    implementation(project(path = ":baseui"))
    implementation(project(path = ":infrastructure:components:android"))
    implementation(project(path = ":infrastructure:components:kotlin"))
    testImplementation(project(path = ":test-tools"))
    testImplementation(libs.bundles.test.default)
}
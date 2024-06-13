plugins {
    id("wottrich.github.io.smartchecklist.feature.impl")
}

android {
    namespace = "wottrich.github.io.smartchecklist.uiprivacypolicy"
}

dependencies {
    implementation(libs.bundles.compose.default)
    implementation(libs.bundles.koin.default)
    implementation(project(path = ":datasource:public"))
    implementation(project(path = ":baseui"))
    testImplementation(project(path = ":test-tools"))
    testImplementation(libs.bundles.test.default)
}
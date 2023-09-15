plugins {
    id("wottrich.github.io.smartchecklist.feature.impl")
}

android {
    namespace = "wottrich.github.io.smartchecklist.suggestion"
}

dependencies {
    api(project(path = ":features:suggestion:public"))
    api(project(path = ":features:suggestion:contract"))
    implementation(libs.bundles.koin.default)
    implementation(libs.bundles.compose.default)
    implementation(libs.bundles.compose.navigation.default)
    implementation(project(path = ":baseui"))
    implementation(project(path = ":common-ui-compose"))
    implementation(project(path = ":datasource"))
    implementation(project(path = ":domain:coroutines"))
    implementation(project(path = ":infrastructure:components:android"))
    implementation(project(path = ":infrastructure:components:kotlin"))
    // TODO move it to version catalogs when test complete with success
    implementation("androidx.constraintlayout:constraintlayout-compose:1.0.1")

    testImplementation(project(path = ":test-tools"))
    testImplementation(libs.bundles.test.default)
}
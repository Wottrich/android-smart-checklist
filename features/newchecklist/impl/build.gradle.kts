plugins {
    id("wottrich.github.io.smartchecklist.feature.impl")
}

android {
    namespace = "github.io.wottrich.newchecklist.impl"
}

dependencies {
    api(project(path = ":features:checklist:public"))
    implementation(libs.bundles.koin.default)
    implementation(libs.bundles.compose.default)
    implementation(libs.bundles.compose.navigation.default)
    implementation(project(path = ":domain:coroutines"))
    implementation(project(path = ":datasource"))
    implementation(project(path = ":baseui"))
    implementation(project(path = ":infrastructure:components:android"))
    implementation(project(path = ":infrastructure:components:kotlin"))
    testImplementation(project(path = ":test-tools"))
    testImplementation(libs.bundles.test.default)
}
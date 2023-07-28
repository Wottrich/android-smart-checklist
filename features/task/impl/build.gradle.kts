plugins {
    id("wottrich.github.io.smartchecklist.feature.impl")
}

android {
    namespace = "wottrich.github.io.smartchecklist.task"
}

dependencies {
    api(project(path = ":features:task:public"))
    implementation(libs.bundles.koin.default)
    implementation(libs.bundles.compose.default)
    implementation(libs.bundles.compose.navigation.default)

    implementation(project(path = ":domain:coroutines"))
    implementation(project(path = ":baseui"))
    implementation(project(path = ":datasource"))
    implementation(project(path = ":infrastructure:components:android"))
    implementation(project(path = ":infrastructure:components:kotlin"))
    implementation(project(path = ":common-ui-compose"))

    testImplementation(project(path = ":test-tools"))
    testImplementation(libs.bundles.test.default)
}
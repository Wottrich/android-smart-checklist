plugins {
    id("wottrich.github.io.smartchecklist.feature.impl")
}

android {
    namespace = "wottrich.github.io.smartchecklist.newchecklist"
}

dependencies {
    api(project(path = ":features:newchecklist:public"))
    implementation(libs.bundles.koin.default)
    implementation(libs.bundles.compose.default)
    implementation(libs.bundles.compose.navigation.default)
    implementation(project(path = ":domain:coroutines"))
    implementation(project(path = ":datasource:public"))
    implementation(project(path = ":baseui"))
    implementation(project(path = ":infrastructure:components:android"))
    implementation(project(path = ":infrastructure:components:kotlin"))
    implementation(project(path = ":features:checklist:public"))
    testImplementation(project(path = ":test-tools"))
    testImplementation(libs.bundles.test.default)
}
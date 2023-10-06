plugins {
    id("wottrich.github.io.smartchecklist.feature.impl")
}

android {
    namespace = "wottrich.github.io.smartchecklist.quicklychecklist"
}

dependencies {
    implementation(libs.bundles.koin.default)
    implementation(libs.bundles.compose.default)
    implementation(libs.bundles.compose.navigation.default)
    implementation(libs.gson)

    implementation(project(path = ":baseui"))
    implementation(project(path = ":datasource"))
    implementation(project(path = ":domain:coroutines"))
    implementation(project(path = ":infrastructure:generator:uuid"))
    implementation(project(path = ":infrastructure:components:android"))
    implementation(project(path = ":infrastructure:components:kotlin"))
    implementation(project(path = ":infrastructure:extensions:intent"))
    // TODO fix this wrong implementation!!!
    implementation(project(path = ":features:task:impl"))
    implementation(project(path = ":features:newchecklist:impl"))

    testImplementation(libs.bundles.test.default)
}
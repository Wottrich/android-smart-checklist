plugins {
    id("wottrich.github.io.smartchecklist.feature.impl")
}

android {
    namespace = "github.io.wottrich.checklist"
}

dependencies {
    api(project(path = ":features:checklist:public"))
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.bundles.koin.default)
    implementation(project(path = ":domain:coroutines"))
    implementation(project(path = ":datasource"))
    implementation(project(path = ":baseui"))
    testImplementation(project(path = ":test-tools"))
    testImplementation(libs.bundles.test.default)
}
plugins {
    id("wottrich.github.io.smartchecklist.feature.impl")
    id("wottrich.github.io.smartchecklist.compose")
}

android {
    namespace = "wottrich.github.io.smartchecklist.serversync"
}

dependencies {
    api(project(path = ":features:server-sync:public"))
    implementation(project(path = ":features:backup:public"))
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.bundles.koin.default)
    implementation(libs.bundles.compose.default)
    implementation(libs.bundles.compose.navigation.default)
    implementation(libs.android.activity.compose)
    implementation(project(path = ":domain:coroutines"))
    implementation(project(path = ":datasource:public"))
    implementation(project(path = ":infrastructure:components:android"))
    implementation(project(path = ":infrastructure:components:kotlin"))
    implementation(project(path = ":baseui"))

    // Server sync (self-hosted Ktor backend) — networked feature, user-configured.
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.work.runtime.ktx)

    testImplementation(project(path = ":test-tools"))
    testImplementation(libs.bundles.test.default)
}

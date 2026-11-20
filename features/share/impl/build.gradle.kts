plugins {
    id("wottrich.github.io.smartchecklist.feature.impl")
    id("wottrich.github.io.smartchecklist.compose")
    alias(libs.plugins.ksp)
}

android {
    namespace = "wottrich.github.io.smartchecklist.share"
}

dependencies {
    api(project(path = ":features:share:public"))
    implementation(project(path = ":features:server-sync:public"))
    implementation(project(path = ":features:server-sync:impl"))
    implementation(project(path = ":features:backup:public"))
    implementation(project(path = ":features:checklist:public"))
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.bundles.koin.default)
    implementation(libs.bundles.compose.default)
    implementation(libs.bundles.compose.navigation.default)
    implementation(libs.android.activity.compose)
    implementation(project(path = ":domain:coroutines"))
    implementation(project(path = ":datasource:public"))
    implementation(project(path = ":infrastructure:components:android"))
    implementation(project(path = ":infrastructure:components:kotlin"))
    implementation(project(path = ":infrastructure:generator:uuid"))
    implementation(project(path = ":baseui"))

    // Embedded share host (Ktor server, CIO) + client — networked feature, user-configured.
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.work.runtime.ktx)

    // share_host.db — the isolated host-side change log (separate Room file)
    implementation(libs.bundles.room.default)
    ksp(libs.room.compiler)

    testImplementation(project(path = ":test-tools"))
    testImplementation(libs.bundles.test.default)
}

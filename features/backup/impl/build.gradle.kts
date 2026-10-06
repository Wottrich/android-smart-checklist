plugins {
    id("wottrich.github.io.smartchecklist.feature.impl")
    id("wottrich.github.io.smartchecklist.compose")
}

android {
    namespace = "wottrich.github.io.smartchecklist.backup"
}

dependencies {
    api(project(path = ":features:backup:public"))
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

    // Google Drive backup (issue #94) — the only networked feature of the app.
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coroutines.play.services)
    implementation(libs.play.services.auth) {
        exclude(group = "com.google.guava", module = "listenablefuture")
    }
    implementation(libs.google.api.client.android) {
        exclude(group = "com.google.guava", module = "listenablefuture")
    }
    implementation(libs.google.api.services.drive) {
        exclude(group = "com.google.guava", module = "listenablefuture")
    }

    testImplementation(project(path = ":test-tools"))
    testImplementation(libs.bundles.test.default)
}

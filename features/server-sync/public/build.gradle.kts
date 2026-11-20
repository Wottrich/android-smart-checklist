plugins {
    id("wottrich.github.io.smartchecklist.feature.public")
    alias(libs.plugins.kotlin.serialization)
}

dependencies {
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.kotlin.stdlib)
    implementation(libs.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.ktor.client.core)
    implementation(project(path = ":domain:coroutines"))
    testImplementation(libs.kotlinx.serialization.json)
    testImplementation(libs.junit)
}

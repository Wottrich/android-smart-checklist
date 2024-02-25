plugins {
    id("wottrich.github.io.smartchecklist.kotlin.lib")
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.coroutines.core)
    implementation(libs.koin.core)
}
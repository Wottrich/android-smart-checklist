plugins {
    id("wottrich.github.io.smartchecklist.android.lib")
    alias(libs.plugins.ksp)
    alias(libs.plugins.jetbrains.kotlin.parcelize)
}

android {
    namespace = "wottrich.github.io.smartchecklist.datasource"
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":features:suggestion:contract"))
    implementation(libs.bundles.koin.default)
    implementation(libs.bundles.room.default)
    ksp(libs.room.compiler)
    implementation(project(path = ":infrastructure:generator:uuid"))
    testImplementation(libs.bundles.test.default)
}
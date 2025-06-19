plugins {
    id("org.jetbrains.kotlin.plugin.compose")
}

private val libs: VersionCatalog = versionCatalogs.named("libs")

dependencies {
    val implementation by configurations
    val debugImplementation by configurations

    implementation(platform(libs.findLibrary("compose.bom").get()))
    implementation(libs.findBundle("compose.default").get())
    debugImplementation(libs.findLibrary("compose.ui.tooling").get())
}
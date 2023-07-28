plugins {
    id("wottrich.github.io.smartchecklist.android.lib")
}

android {
    namespace = "github.io.wottrich.intent"
}

dependencies {
    implementation(libs.android.app.compat)
}
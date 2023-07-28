plugins {
    id("wottrich.github.io.smartchecklist.android.lib")
}

android {
    namespace = "wottrich.github.io.smartchecklist.intent"
}

dependencies {
    implementation(libs.android.app.compat)
}
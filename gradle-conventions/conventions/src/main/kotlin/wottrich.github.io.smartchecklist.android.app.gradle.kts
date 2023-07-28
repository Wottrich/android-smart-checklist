
plugins {
    id("com.android.application")
    id("wottrich.github.io.smartchecklist.base.android")
}

android {
    buildTypes {
        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
        }
        getByName("debug") {
            applicationIdSuffix = ".debug"
            isDebuggable = true
            versionNameSuffix = "-debug"
        }
    }
}
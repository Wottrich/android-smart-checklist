import org.gradle.kotlin.dsl.the

plugins {
    id("wottrich.github.io.smartchecklist.android.lib")
}

internal val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()

android {
    buildFeatures.compose = true
    composeOptions.kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
}
import gradle.kotlin.dsl.accessors._404981569cb7bc4f1f0ba7441ad57f27.android
import org.gradle.kotlin.dsl.the

plugins {
    id("wottrich.github.io.smartchecklist.android.lib")
}

internal val libs = the<org.gradle.accessors.dm.LibrariesForLibs>()

android {
    buildFeatures.compose = true
    composeOptions.kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
}
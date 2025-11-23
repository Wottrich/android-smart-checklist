import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    `kotlin-dsl-precompiled-script-plugins`
}

repositories {
    google()
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.gradlePlugins.compose.compiler)
    implementation("com.android.tools.build:gradle:${libs.versions.gradle.get()}")
}

kotlin {
    sourceSets.all {
        jvmToolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
            vendor.set(JvmVendorSpec.AZUL)
        }

        languageSettings {
            languageVersion = KotlinVersion.KOTLIN_1_8.version
        }
    }
}
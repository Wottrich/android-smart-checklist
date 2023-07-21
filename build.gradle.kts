// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version Versions.gradleVersion apply false
    id("com.android.library") version Versions.gradleVersion apply false
    id("org.jetbrains.kotlin.android") version Versions.kotlinVersion apply false
    id("org.jetbrains.kotlin.jvm") version Versions.kotlinVersion apply false
    id(Plugins.kspPlugin) version Versions.kspVersion apply false
}
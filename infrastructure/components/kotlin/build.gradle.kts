import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

plugins {
    id(Plugins.javaLibrary)
    id(Plugins.kotlinPlugin)
}

tasks.withType<JavaCompile> {
    sourceCompatibility = AndroidSdk.javaVersion.toString()
    targetCompatibility = AndroidSdk.javaVersion.toString()
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = AndroidSdk.javaVersion.toString()
    }
}

kotlinExtension.jvmToolchain {
    languageVersion.set(JavaLanguageVersion.of(17))
}

dependencies {
    coroutines()
}
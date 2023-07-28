plugins {
    id("wottrich.github.io.smartchecklist.feature.public")
}

dependencies {
    implementation(libs.kotlin.stdlib.jdk8)
    implementation(libs.kotlin.stdlib)
    implementation(project(path = ":domain:coroutines"))
}
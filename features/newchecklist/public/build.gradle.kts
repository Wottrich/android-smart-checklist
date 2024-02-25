plugins {
    id("wottrich.github.io.smartchecklist.feature.public")
}

dependencies {
    implementation(project(":domain:coroutines"))
    implementation(project(":datasource:public"))
}
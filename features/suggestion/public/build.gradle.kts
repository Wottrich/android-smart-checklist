plugins {
    id("wottrich.github.io.smartchecklist.feature.public")
}

dependencies {
    implementation(project(path = ":features:suggestion:contract"))
    implementation(project(path = ":domain:coroutines"))
}
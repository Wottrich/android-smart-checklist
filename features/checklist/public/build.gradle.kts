plugins {
    id(Plugins.javaLibrary)
    id(Plugins.kotlinPlugin)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

dependencies {

    moduleDomainCoroutines()

}
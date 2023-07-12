// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    repositories {
        google()
        mavenCentral()
        jcenter()
    }
    dependencies {
        defaultClasspath()
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}
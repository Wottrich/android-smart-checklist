// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    // OLD VERSIONS
//    ext {
//
//        java_version = JavaVersion.VERSION_1_8
//
//        version_name = "1.3.0"
//        version_code = 5
//
//        //Kotlin
//        kotlin_version = "1.5.31"
//        core_ktx_version = "1.6.0"
//
//        //Coroutines
//        coroutines_version = "1.3.9"
//
//        //AppCompat and Ui things
//        app_compat_version = "1.3.1"
//        constraint_layout_version = "2.1.0"
//        material_version = "1.4.0"
//
//        //Navigation
//        navigation_version = "2.3.5"
//
//        //Android
//        lifecycle_extensions_version = "2.2.0"
//        lifecycle_viewmodel_version = "2.3.1"
//        livedata_lifecycle_version = "2.4.0-alpha03"
//
//        //Compose
//        compose_version = '1.1.0-beta02'
//        compose_kotlin_compiler = '1.1.0-beta02'
//        navigation_compose_version = '2.4.0-rc01'
//        navigation_animation_accompanist_version = '0.22.0-rc'
//        system_ui_controller = '0.24.6-alpha'
//        compose_activity_version = '1.3.0-alpha05'
//
//        lifecycle_runtime_ktx_version = '2.3.1'
//
//        //Koin
//        koin_version = "3.1.2"
//
//        //Database
//        room_version = "2.3.0"
//
//        //Test
//        junit_version = "4.13.2"
//        mockk_version = "1.12.1"
//        core_testing_version = "2.1.0"
//
//        //Gradle
//        gradle_version = '7.0.0'
//
//        //Test instrumental
//        junit_ext_version = "1.1.3"
//        espresso_version = "3.4.0"
//
//    }

    repositories {
        google()
        mavenCentral()
        jcenter()
    }
    dependencies {
        defaultClasspath()
        //classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navigation_version")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

//task clean(type: Delete) {
//    delete rootProject.buildDir
//}
//android {
//    compileSdk = 36
//
//    defaultConfig {
//        minSdk = 23
//        targetSdk = 36
//
//        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//        consumerProguardFiles("consumer-rules.pro")
//    }
//
//    buildTypes {
//        getByName("release") {
//            isMinifyEnabled = false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
//        }
//    }
//
//    compileOptions {
//        sourceCompatibility = JavaVersion.VERSION_17
//        targetCompatibility = JavaVersion.VERSION_17
//    }
//
//    kotlin {
//        compilerOptions {
//            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
//        }
//        jvmToolchain {
//            languageVersion.set(JavaLanguageVersion.of(17))
//            vendor.set(JvmVendorSpec.AZUL)
//        }
//    }
//}
//
//tasks.withType<JavaCompile> {
//    sourceCompatibility = JavaVersion.VERSION_17.toString()
//    targetCompatibility = JavaVersion.VERSION_17.toString()
//}
//
//dependencies {
//    val implementation by configurations
//
//    implementation(libs.kotlin.stdlib)
//    implementation(libs.android.core.ktx)
//}

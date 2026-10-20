//import com.android.build.api.dsl.ApplicationExtension
//import com.android.build.api.dsl.CommonExtension
//import com.android.build.api.dsl.LibraryExtension
//import org.gradle.api.JavaVersion
//import org.gradle.api.Project
//import org.gradle.api.artifacts.VersionCatalogsExtension
//import org.gradle.api.tasks.compile.JavaCompile
//import org.gradle.jvm.toolchain.JavaLanguageVersion
//import org.gradle.jvm.toolchain.JvmVendorSpec
//import org.gradle.kotlin.dsl.configure
//import org.gradle.kotlin.dsl.dependencies
//import org.gradle.kotlin.dsl.getByType
//import org.gradle.kotlin.dsl.withType
//import org.jetbrains.kotlin.gradle.dsl.JvmTarget
//import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
//
//fun Project.configureSharedAndroidOptions() {
//    pluginManager.withPlugin("com.android.base") {
//        extensions.configure<CommonExtension> {
//            compileSdk = 36
//
//            defaultConfig {
//                minSdk = 23
//                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
//            }
//
//            buildTypes {
//                getByName("release") {
//                    isMinifyEnabled = false
//                    proguardFiles(
//                        getDefaultProguardFile("proguard-android-optimize.txt"),
//                        "proguard-rules.pro"
//                    )
//                }
//            }
//
//            compileOptions {
//                sourceCompatibility = JavaVersion.VERSION_17
//                targetCompatibility = JavaVersion.VERSION_17
//            }
//        }
//    }
//
//    pluginManager.withPlugin("com.android.library") {
//        extensions.configure<LibraryExtension> {
//            defaultConfig {
//                consumerProguardFiles("consumer-rules.pro")
//            }
//        }
//    }
//
//    pluginManager.withPlugin("com.android.application") {
//        extensions.configure<ApplicationExtension> {
//            defaultConfig {
//                targetSdk = 36
//            }
//        }
//    }
//
//    pluginManager.withPlugin("org.jetbrains.kotlin.android") {
//        extensions.configure<KotlinAndroidProjectExtension> {
//            compilerOptions {
//                jvmTarget.set(JvmTarget.JVM_17)
//            }
//            jvmToolchain {
//                languageVersion.set(JavaLanguageVersion.of(17))
//                vendor.set(JvmVendorSpec.AZUL)
//            }
//        }
//    }
//
//    tasks.withType<JavaCompile>().configureEach {
//        sourceCompatibility = JavaVersion.VERSION_17.toString()
//        targetCompatibility = JavaVersion.VERSION_17.toString()
//    }
//
//    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
//    dependencies {
//        val implementation = "implementation"
//
//        libs.findLibrary("kotlin.stdlib").ifPresent { add(implementation, it) }
//        libs.findLibrary("android.core.ktx").ifPresent { add(implementation, it) }
//    }
//}
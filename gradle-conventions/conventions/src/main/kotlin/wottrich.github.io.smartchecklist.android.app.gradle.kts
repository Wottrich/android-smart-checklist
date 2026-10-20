import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

plugins {
    id("com.android.application")
}

// Release signing is read from `keystore.properties` at the repo root (gitignored).
// When the file is absent or incomplete, release falls back to debug signing so
// local machines can still build; CI always provides the real keystore.
val keystoreProps = Properties()
val keystorePropertiesFile = rootProject.file("keystore.properties")
val requiredKeys = listOf("storeFile", "storePassword", "keyAlias", "keyPassword")
val hasReleaseKeystore = keystorePropertiesFile.isFile && keystorePropertiesFile.inputStream().use { stream ->
    keystoreProps.load(stream)
    val allKeysPresent = requiredKeys.none { keystoreProps.getProperty(it).isNullOrBlank() }
    val storeFileExists = rootProject.file(keystoreProps.getProperty("storeFile") ?: "").isFile
    allKeysPresent && storeFileExists
}
if (keystorePropertiesFile.isFile && !hasReleaseKeystore) {
    logger.lifecycle(
        "android.app: keystore.properties is incomplete or its storeFile does not exist — " +
            "release builds fall back to debug signing"
    )
}

extensions.configure<ApplicationExtension> {
    compileSdk = 36

    defaultConfig {
        minSdk = 23
        targetSdk = 36
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    if (hasReleaseKeystore) {
        signingConfigs {
            create("release") {
                storeFile = rootProject.file(keystoreProps.getProperty("storeFile"))
                storePassword = keystoreProps.getProperty("storePassword")
                keyAlias = keystoreProps.getProperty("keyAlias")
                keyPassword = keystoreProps.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = if (hasReleaseKeystore) {
                signingConfigs.getByName("release")
            } else {
                signingConfigs.getByName("debug")
            }
        }
        getByName("debug") {
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            isDebuggable = true
            versionNameSuffix = "-debug"
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

// Resolvendo dependências no plugin de convenção com segurança
val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
dependencies {
    add("implementation", libs.findLibrary("kotlin.stdlib").get())
    add("implementation", libs.findLibrary("android.core.ktx").get())
}
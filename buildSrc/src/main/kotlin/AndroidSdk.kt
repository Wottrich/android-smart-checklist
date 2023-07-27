import java.io.File
import org.gradle.api.JavaVersion
import org.gradle.api.plugins.PluginAware
import org.gradle.kotlin.dsl.apply

object AndroidSdk {
    const val targetSdk = 33
    const val minSdk = 21
    const val buildToolsVersion = "33.0.1"
    val javaVersion = JavaVersion.VERSION_17
    val jvmTarget = JavaVersion.VERSION_17
}
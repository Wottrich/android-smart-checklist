import java.io.File
import org.gradle.api.JavaVersion
import org.gradle.api.plugins.PluginAware
import org.gradle.kotlin.dsl.apply

private const val commonLibraryName = "common-library.gradle.kts"

object AndroidSdk {
    const val targetSdk = 31
    const val minSdk = 21
    const val buildToolsVersion = "30.0.3"
    val javaVersion = JavaVersion.VERSION_1_8
    val jvmTarget = JavaVersion.VERSION_1_8
}

fun PluginAware.applyDefaultLibraryConfiguration(file: File) {
    apply(from = "$file/$commonLibraryName")
}
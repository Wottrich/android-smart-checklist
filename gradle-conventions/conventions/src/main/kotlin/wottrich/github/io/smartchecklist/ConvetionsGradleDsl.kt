import com.android.build.gradle.BaseExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions

internal val Project.android: BaseExtension
    get() = this.extensions["android"] as BaseExtension

internal fun Project.android(block: Action<BaseExtension>) {
    block.execute(android)
}

internal fun BaseExtension.kotlinOptions(configure: KotlinJvmOptions.() -> Unit) {
    (this as ExtensionAware).extensions.configure(KotlinJvmOptions::class.java, configure)
}
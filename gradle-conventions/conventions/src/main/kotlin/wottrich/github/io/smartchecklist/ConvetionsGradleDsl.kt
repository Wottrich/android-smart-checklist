import com.android.build.gradle.BaseExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get

internal val Project.android: BaseExtension
    get() = this.extensions["android"] as BaseExtension

internal fun Project.android(block: Action<BaseExtension>) {
    block.execute(android)
}
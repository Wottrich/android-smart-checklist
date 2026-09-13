import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.kotlin.dsl.get

//internal val Project.android: CommonExtension
//    get() = this.extensions["android"] as CommonExtension
//
//internal fun Project.android(block: Action<CommonExtension>) {
//    block.execute(android)
//}
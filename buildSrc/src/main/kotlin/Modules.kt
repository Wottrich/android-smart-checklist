import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.project

fun DependencyHandlerScope.moduleDatasource() {
    "implementation"(project(":datasource"))
}

fun DependencyHandlerScope.moduleData() {
    "implementation"(project(":data"))
}

fun DependencyHandlerScope.moduleCommonUiCompose() {
    "implementation"(project(":common-ui-compose"))
}

// TODO Will dead soon
fun DependencyHandlerScope.moduleTools() {
    "implementation"(project(":tools"))
}

fun DependencyHandlerScope.moduleUiHome() {
    "implementation"(project(":ui-home"))
}

fun DependencyHandlerScope.moduleCommonResources() {
    "implementation"(project(":common-resources"))
}

fun DependencyHandlerScope.moduleCommonAndroid() {
    "implementation"(project(":common-android"))
}

fun DependencyHandlerScope.moduleDomain() {
    "implementation"(project(":domain"))
}
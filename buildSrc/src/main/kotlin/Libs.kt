import org.gradle.kotlin.dsl.DependencyHandlerScope

object Libs {

    //Kotlin
    const val kotlinStdlibJdk8 = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlinVersion}"
    const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinVersion}"
    const val androidCoreKtx = "androidx.core:core-ktx:${Versions.coreKtxVersion}"
    const val coroutinesLib = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutinesVersion}"
    const val coroutinesTestLib = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutinesVersion}"
    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompatVersion}"
    const val lifecycleRuntime = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycleRuntime}"
    const val lifecycleViewModelKtx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycleViewModelKtx}"
    const val insertKoinCore = "io.insert-koin:koin-core:${Versions.koinVersion}"
    const val insertKoinAndroid = "io.insert-koin:koin-android:${Versions.koinVersion}"
    const val insertKoinAndroidCompose = "io.insert-koin:koin-androidx-compose:${Versions.koinVersion}"
    const val insertKoinTest = "io.insert-koin:koin-test:${Versions.koinVersion}"
    const val composeUi = "androidx.compose.ui:ui:${Versions.composeVersion}"
    const val composeUiTooling = "androidx.compose.ui:ui-tooling:${Versions.composeVersion}"
    const val composeRuntime = "androidx.compose.runtime:runtime:${Versions.composeVersion}"
    const val composeMaterial = "androidx.compose.material:material:${Versions.composeVersion}"
    const val composeActivity = "androidx.activity:activity-compose:${Versions.composeActivityVersion}"
    const val composeCompiler = "androidx.compose.compiler:compiler:${Versions.composeVersion}"
    const val placeholder = "com.google.accompanist:accompanist-placeholder:${Versions.accompanistVersion}"
    const val systemUiControllerAccompanist = "com.google.accompanist:accompanist-systemuicontroller:${Versions.systemUiControllerVersion}"
    const val composeNavigation = "androidx.navigation:navigation-compose:${Versions.composeNavigationVersion}"
    const val composeNavigationAnimationAccompanist = "com.google.accompanist:accompanist-navigation-animation:${Versions.composeNavigationAnimationAccompanistVersion}"
    const val composeNavigationAccompanist = "com.google.accompanist:accompanist-navigation-material:${Versions.composeNavigationAccompanistVersion}"
    const val coilCompose = "io.coil-kt:coil-compose:${Versions.coilVersion}"
    const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofitVersion}"
    const val converterGson = "com.squareup.retrofit2:converter-gson:${Versions.retrofitVersion}"
    const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.loggingInterceptorVersion}"
    const val roomRuntime = "androidx.room:room-runtime:${Versions.room}"
    const val roomKtx = "androidx.room:room-ktx:${Versions.room}"
    const val roomCompiler = "androidx.room:room-compiler:${Versions.room}"
    const val androidMaterial = "com.google.android.material:material:${Versions.androidMaterial}"
    const val mockK = "io.mockk:mockk:${Versions.mockKVersion}"
    const val coreTesting = "androidx.arch.core:core-testing:${Versions.coreTestingVersion}"
    const val junit = "junit:junit:${Versions.junitVersion}"
    const val junitExt = "androidx.test.ext:junit:${Versions.junitExtVersion}"
    const val espressoCore = "androidx.test.espresso:espresso-core:${Versions.espressoCoreVersion}"

}

fun DependencyHandlerScope.kotlinDefault() {
    "implementation"(Libs.kotlinStdlib)
}

fun DependencyHandlerScope.kotlinAndCoreKtx() {
    kotlinDefault()
    "implementation"(Libs.androidCoreKtx)
}

fun DependencyHandlerScope.composeUi() {
    "implementation"(Libs.composeUi)
    "implementation"(Libs.composeUiTooling)
    "implementation"(Libs.composeRuntime)
    "implementation"(Libs.composeMaterial)
    "implementation"(Libs.composeActivity)
    "implementation"(Libs.composeCompiler)
    "implementation"(Libs.placeholder)
}

fun DependencyHandlerScope.lifecycleLibs() {
    "implementation"(Libs.lifecycleRuntime)
}

fun DependencyHandlerScope.coroutines() {
    "implementation"(Libs.coroutinesLib)
    "implementation"(Libs.coroutinesTestLib)
}

fun DependencyHandlerScope.koinCore() {
    "implementation"(Libs.insertKoinCore)
}

fun DependencyHandlerScope.koin() {
    koinCore()
    "implementation"(Libs.insertKoinAndroid)
    "implementation"(Libs.insertKoinAndroidCompose)
}

fun DependencyHandlerScope.navigation() {
    "implementation"(Libs.composeNavigation)
    "implementation"(Libs.composeNavigationAccompanist)
    "implementation"(Libs.composeNavigationAnimationAccompanist)
}

fun DependencyHandlerScope.unitTest(asImplementation: Boolean = false) {
    val expression = if (asImplementation) "implementation" else "testImplementation"
    expression(Libs.mockK)
    expression(Libs.coreTesting)
    expression(Libs.junit)
    expression(Libs.insertKoinTest)
}

fun DependencyHandlerScope.instrumentalTest(asImplementation: Boolean = true) {
    val expression = if (asImplementation) "implementation" else "androidTestImplementation"
    expression(Libs.junitExt)
    expression(Libs.espressoCore)
}

fun DependencyHandlerScope.apiDigest() {
    "implementation"(Libs.retrofit)
    "implementation"(Libs.converterGson)
    "implementation"(Libs.loggingInterceptor)
}

fun DependencyHandlerScope.gson() {
    "implementation"(Libs.converterGson)
}

fun DependencyHandlerScope.roomKtx() {
    "implementation"(Libs.roomKtx)
}

fun DependencyHandlerScope.room(withCompiler: Boolean = false) {
    "implementation"(Libs.roomRuntime)
    roomKtx()
    if (withCompiler) {
        "ksp"(Libs.roomCompiler)
    }
}
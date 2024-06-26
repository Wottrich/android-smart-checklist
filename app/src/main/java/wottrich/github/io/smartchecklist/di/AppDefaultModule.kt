package wottrich.github.io.smartchecklist.di

import android.content.ClipboardManager
import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import org.koin.java.KoinJavaComponent.getKoin
import wottrich.github.io.smartchecklist.BuildConfig
import wottrich.github.io.smartchecklist.android.SmartChecklistNavigation
import wottrich.github.io.smartchecklist.navigation.AppNavigator
import wottrich.github.io.smartchecklist.navigation.OpenPlayStoreNavigator
import wottrich.github.io.smartchecklist.navigation.OpenPlayStoreNavigatorImpl
import wottrich.github.io.smartchecklist.navigation.PrivacyPolicy
import wottrich.github.io.smartchecklist.navigation.PrivacyPolicyImpl

val appDefaultModule = module {
    single<ClipboardManager> {
        androidContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    }
    single<OpenPlayStoreNavigator> { OpenPlayStoreNavigatorImpl(androidContext()) }
    single<PrivacyPolicy> { PrivacyPolicyImpl(androidContext()) }
    single { AppNavigator(getAll<SmartChecklistNavigation>()) }

    getKoin().setProperty(AppProperties.VERSION_NAME, BuildConfig.VERSION_NAME)
    getKoin().setProperty(AppProperties.VERSION_CODE, BuildConfig.VERSION_CODE.toString())
}
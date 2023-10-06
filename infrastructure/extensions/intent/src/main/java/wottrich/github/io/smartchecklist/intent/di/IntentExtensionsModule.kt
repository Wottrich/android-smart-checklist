package wottrich.github.io.smartchecklist.intent.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import wottrich.github.io.smartchecklist.intent.navigation.ShareIntentTextNavigator
import wottrich.github.io.smartchecklist.intent.navigation.ShareIntentTextNavigatorImpl

val intentExtensionsModule = module {
    single<ShareIntentTextNavigator> { ShareIntentTextNavigatorImpl(androidContext()) }
}
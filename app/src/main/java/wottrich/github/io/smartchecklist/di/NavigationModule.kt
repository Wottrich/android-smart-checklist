package wottrich.github.io.smartchecklist.di

import org.koin.dsl.bind
import org.koin.dsl.module
import wottrich.github.io.smartchecklist.android.SmartChecklistNavigation
import wottrich.github.io.smartchecklist.navigation.HomeContextNavigator

val navigationModule = module {
    single {
        HomeContextNavigator(
            openPlayStoreNavigator = get(),
            privacyPolicy = get(),
            versionName = getProperty(AppProperties.VERSION_NAME),
            versionCode = getProperty(AppProperties.VERSION_CODE)
        )
    } bind SmartChecklistNavigation::class
}
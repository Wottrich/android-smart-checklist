package wottrich.github.io.androidsmartchecklist

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import wottrich.github.io.androidsmartchecklist.injection.AppModule

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 12/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */
 
class SmartChecklistApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@SmartChecklistApplication)
            modules(AppModule.appModule)
        }
    }

}
package wottrich.github.io.smartchecklist

import android.app.Application
import org.koin.android.ext.koin.androidContext
import wottrich.github.io.smartchecklist.serversync.api.SyncScheduler
import org.koin.core.context.startKoin
import wottrich.github.io.smartchecklist.di.AppModule

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
        // Server-sync safety net: repeats every 15 min on unmetered networks.
        // The worker exits early while no server is configured, so this is always safe.
        org.koin.core.context.GlobalContext.get().get<SyncScheduler>().schedulePeriodicSync()
    }

}
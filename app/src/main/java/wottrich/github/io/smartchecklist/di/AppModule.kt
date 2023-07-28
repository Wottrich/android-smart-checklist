package wottrich.github.io.smartchecklist.di

import github.io.wottrich.checklist.di.checklistModule
import github.io.wottrich.coroutines.di.coroutinesModule
import github.io.wottrich.newchecklist.di.newChecklistModule
import github.io.wottrich.ui.support.di.supportModule
import wottrich.github.io.smartchecklist.datasource.injection.databaseModule
import wottrich.github.io.impl.di.taskModule
import wottrich.github.io.quicklychecklist.impl.di.quicklyChecklistModule

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 12/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */
 
object AppModule {

    val appModule = listOf(
        appDefaultModule,

        //Splash
        splashModule,

        //Features
        featureHomeModules,
        checklistModule,
        taskModule,
        supportModule,
        newChecklistModule,
        quicklyChecklistModule,

        //Database
        databaseModule,

        //Domain,
        coroutinesModule
    )

}
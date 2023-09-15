package wottrich.github.io.smartchecklist.di

import wottrich.github.io.smartchecklist.checklist.di.checklistModule
import wottrich.github.io.smartchecklist.coroutines.di.coroutinesModule
import wottrich.github.io.smartchecklist.newchecklist.di.newChecklistModule
import wottrich.github.io.smartchecklist.uisupport.di.supportModule
import wottrich.github.io.smartchecklist.datasource.injection.databaseModule
import wottrich.github.io.smartchecklist.di.taskModule
import wottrich.github.io.smartchecklist.quicklychecklist.di.quicklyChecklistModule
import wottrich.github.io.smartchecklist.suggestion.di.suggestionModule

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
        suggestionModule,

        //Database
        databaseModule,

        //Domain,
        coroutinesModule
    )

}
package wottrich.github.io.androidsmartchecklist.injection

import wottrich.github.io.database.injection.databaseModule
import wottrich.github.io.featurenew.injection.featureNewModule
import wottrich.github.io.tools.injection.toolsDispatcherModule

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

        //Features
        featureHomeModules,
        featureNewModule,

        //Database
        databaseModule,

        //Tools
        toolsDispatcherModule
    )

}
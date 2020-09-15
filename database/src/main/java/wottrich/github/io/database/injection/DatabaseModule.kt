package wottrich.github.io.database.injection

import org.koin.dsl.module
import wottrich.github.io.database.AppDatabase

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 13/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */
 
val databaseModule = module {

    single { AppDatabase.getInstance(get()).checklistDao() }

}
package wottrich.github.io.database.injection

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import wottrich.github.io.database.AppDatabase
import wottrich.github.io.database.dao.ChecklistDao
import wottrich.github.io.database.dao.TaskDao

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 13/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */
 
val databaseModule = module {

    single<ChecklistDao> { AppDatabase.getInstance(androidContext()).checklistDao() }
    single<TaskDao> { AppDatabase.getInstance(androidContext()).taskDao() }

}
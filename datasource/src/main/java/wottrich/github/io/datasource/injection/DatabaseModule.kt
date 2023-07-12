package wottrich.github.io.datasource.injection

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import wottrich.github.io.datasource.AppDatabase
import wottrich.github.io.datasource.dao.ChecklistDao
import wottrich.github.io.datasource.dao.TaskDao
import wottrich.github.io.datasource.repository.ChecklistRepository
import wottrich.github.io.datasource.repository.ChecklistRepositoryImpl

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
    factory<ChecklistRepository> { ChecklistRepositoryImpl(get()) }

}
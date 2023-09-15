package wottrich.github.io.smartchecklist.datasource.injection

import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module
import wottrich.github.io.smartchecklist.datasource.AppDatabase
import wottrich.github.io.smartchecklist.datasource.dao.ChecklistDao
import wottrich.github.io.smartchecklist.datasource.dao.TagDao
import wottrich.github.io.smartchecklist.datasource.dao.TaskDao
import wottrich.github.io.smartchecklist.datasource.repository.ChecklistRepository
import wottrich.github.io.smartchecklist.datasource.repository.ChecklistRepositoryImpl

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 13/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */

private const val APP_DATABASE_KEY = "AppDatabaseInstance"

private fun Scope.getAppDatabaseInstance() = get<AppDatabase>(named(APP_DATABASE_KEY))

val databaseModule = module {

    single(named(APP_DATABASE_KEY)) { AppDatabase.getInstance(androidContext()) }
    single<ChecklistDao> { getAppDatabaseInstance().checklistDao() }
    single<TaskDao> { getAppDatabaseInstance().taskDao() }
    single<TagDao> { getAppDatabaseInstance().tagDao() }
    factory<ChecklistRepository> { ChecklistRepositoryImpl(get()) }

}
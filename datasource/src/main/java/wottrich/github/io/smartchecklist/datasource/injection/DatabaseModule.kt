package wottrich.github.io.smartchecklist.datasource.injection

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import wottrich.github.io.smartchecklist.datasource.AppDatabase
import wottrich.github.io.smartchecklist.datasource.dao.ChecklistDao
import wottrich.github.io.smartchecklist.datasource.dao.SyncOutboxDao
import wottrich.github.io.smartchecklist.datasource.dao.TaskDao
import wottrich.github.io.smartchecklist.datasource.data.datasource.ChecklistDatasource
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncDatasource
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncOutboxDatasource
import wottrich.github.io.smartchecklist.datasource.data.datasource.TaskDatasource
import wottrich.github.io.smartchecklist.datasource.repository.ChecklistDatasourceImpl
import wottrich.github.io.smartchecklist.datasource.repository.SyncDatasourceImpl
import wottrich.github.io.smartchecklist.datasource.repository.SyncOutboxDatasourceImpl
import wottrich.github.io.smartchecklist.datasource.repository.TaskDatasourceImpl

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
    single<SyncOutboxDao> { AppDatabase.getInstance(androidContext()).syncOutboxDao() }
    factory<TaskDatasource> { TaskDatasourceImpl(get()) }
    factory<ChecklistDatasource> { ChecklistDatasourceImpl(get(), get()) }
    factory<SyncOutboxDatasource> { SyncOutboxDatasourceImpl(get()) }
    factory<SyncDatasource> {
        SyncDatasourceImpl(
            database = AppDatabase.getInstance(androidContext()),
            checklistDao = get(),
            taskDao = get()
        )
    }

}

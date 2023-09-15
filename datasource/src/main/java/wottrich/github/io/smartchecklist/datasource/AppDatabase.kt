package wottrich.github.io.smartchecklist.datasource

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import wottrich.github.io.smartchecklist.datasource.converter.Converters
import wottrich.github.io.smartchecklist.datasource.dao.ChecklistDao
import wottrich.github.io.smartchecklist.datasource.dao.TagDao
import wottrich.github.io.smartchecklist.datasource.dao.TaskDao
import wottrich.github.io.smartchecklist.datasource.entity.ChecklistTagsCrossRef
import wottrich.github.io.smartchecklist.datasource.entity.NewChecklist
import wottrich.github.io.smartchecklist.datasource.entity.NewTask
import wottrich.github.io.smartchecklist.datasource.entity.SuggestionEntity
import wottrich.github.io.smartchecklist.datasource.entity.TagEntity
import wottrich.github.io.smartchecklist.datasource.migration.MIGRATION_III_IV
import wottrich.github.io.smartchecklist.datasource.migration.MIGRATION_II_III
import wottrich.github.io.smartchecklist.datasource.migration.MIGRATION_IV_V
import wottrich.github.io.smartchecklist.datasource.migration.MIGRATION_I_II
import wottrich.github.io.smartchecklist.datasource.version.DatabaseVersions

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 13/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */

@Database(
    entities = [NewChecklist::class, NewTask::class, TagEntity::class, SuggestionEntity::class, ChecklistTagsCrossRef::class],
    version = DatabaseVersions.currentVersion,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun checklistDao(): ChecklistDao
    abstract fun taskDao(): TaskDao
    abstract fun tagDao(): TagDao

    companion object {

        private const val DATABASE_NAME = "checklistDatabase2020"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .addMigrations(MIGRATION_I_II)
                .addMigrations(MIGRATION_II_III)
                .addMigrations(MIGRATION_III_IV)
                .addMigrations(MIGRATION_IV_V)
                .build()
        }

    }

}
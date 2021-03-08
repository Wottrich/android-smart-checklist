package wottrich.github.io.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import wottrich.github.io.database.converter.Converters
import wottrich.github.io.database.dao.ChecklistDao
import wottrich.github.io.database.dao.TaskDao
import wottrich.github.io.database.entity.Checklist
import wottrich.github.io.database.entity.Task

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 13/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */

@Database(entities = [Checklist::class, Task::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun checklistDao(): ChecklistDao
    abstract fun taskDao(): TaskDao

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
            return Room.databaseBuilder(
                context, AppDatabase::class.java,
                DATABASE_NAME
            ).build()
        }

    }

}
package wottrich.github.io.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import wottrich.github.io.database.version.DatabaseVersions

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 12/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */
 
val MIGRATION_I_II = object : Migration(DatabaseVersions.I, DatabaseVersions.II) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE checklist ADD COLUMN is_selected BOOLEAN")
    }
}
package wottrich.github.io.smartchecklist.datasource.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import wottrich.github.io.smartchecklist.datasource.version.DatabaseVersions

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
        database.execSQL("ALTER TABLE checklist ADD COLUMN is_selected INTEGER DEFAULT 0 NOT NULL")
    }
}

val MIGRATION_II_III = object : Migration(DatabaseVersions.II, DatabaseVersions.III) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `new_checklist` (`uuid` TEXT NOT NULL, `name` TEXT NOT NULL, `is_selected` INTEGER NOT NULL DEFAULT 0, `created_date` INTEGER NOT NULL, `last_update` INTEGER NOT NULL, PRIMARY KEY(`uuid`))")
        database.execSQL("CREATE TABLE IF NOT EXISTS `new_task` (`uuid` TEXT NOT NULL, `parent_uuid` TEXT NOT NULL, `name` TEXT NOT NULL, `is_completed` INTEGER NOT NULL, `date_created` INTEGER NOT NULL, PRIMARY KEY(`uuid`), FOREIGN KEY(`parent_uuid`) REFERENCES `new_checklist`(`uuid`) ON UPDATE NO ACTION ON DELETE CASCADE )")
    }
}

val MIGRATION_III_IV = object : Migration(DatabaseVersions.III, DatabaseVersions.IV) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE `checklist`")
        database.execSQL("DROP TABLE `task`")
    }
}
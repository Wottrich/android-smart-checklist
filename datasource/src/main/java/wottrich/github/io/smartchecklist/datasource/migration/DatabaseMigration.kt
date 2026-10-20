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

private val MIGRATION_I_II = object : Migration(DatabaseVersions.I, DatabaseVersions.II) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE checklist ADD COLUMN is_selected INTEGER DEFAULT 0 NOT NULL")
    }
}

private val MIGRATION_II_III = object : Migration(DatabaseVersions.II, DatabaseVersions.III) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `new_checklist` (`uuid` TEXT NOT NULL, `name` TEXT NOT NULL, `is_selected` INTEGER NOT NULL DEFAULT 0, `created_date` INTEGER NOT NULL, `last_update` INTEGER NOT NULL, PRIMARY KEY(`uuid`))")
        database.execSQL("CREATE TABLE IF NOT EXISTS `new_task` (`uuid` TEXT NOT NULL, `parent_uuid` TEXT NOT NULL, `name` TEXT NOT NULL, `is_completed` INTEGER NOT NULL, `date_created` INTEGER NOT NULL, PRIMARY KEY(`uuid`), FOREIGN KEY(`parent_uuid`) REFERENCES `new_checklist`(`uuid`) ON UPDATE NO ACTION ON DELETE CASCADE )")
    }
}

private val MIGRATION_III_IV = object : Migration(DatabaseVersions.III, DatabaseVersions.IV) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE `checklist`")
        database.execSQL("DROP TABLE `task`")
    }
}

private val MIGRATION_IV_V = object : Migration(DatabaseVersions.IV, DatabaseVersions.V) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `new_checklist_new` (`uuid` TEXT NOT NULL, `parent_uuid` TEXT, `name` TEXT NOT NULL, `is_selected` INTEGER NOT NULL DEFAULT 0, `created_date` INTEGER NOT NULL, `last_update` INTEGER NOT NULL, PRIMARY KEY(`uuid`). FOREIGN KEY(`parent_uuid`) REFERENCES `new_checklist`(`uuid`) ON UPDATE NO ACTION ON DELETE CASCADE ")
        db.execSQL("""
            INSERT INTO new_checklist_new (uuid, name, is_selected, created_date, last_update)
            SELECT uuid, name, is_selected, created_date, last_update FROM new_checklist
        """.trimIndent())
        db.execSQL("DROP TABLE new_checklist")
        db.execSQL("ALTER TABLE new_checklist_new RENAME TO new_checklist")
        db.execSQL("CREATE INDEX index_checklist_uuid ON new_checklist (uuid)")
        db.execSQL("CREATE INDEX index_task_uuid ON new_task (uuid)")
    }
}

val migrations = arrayOf(
    MIGRATION_I_II,
    MIGRATION_II_III,
    MIGRATION_III_IV,
    MIGRATION_IV_V
)
package wottrich.github.io.smartchecklist.backup.data.backupfile

import kotlinx.serialization.Serializable

/**
 * Root model of the Google Drive backup file (`smart-checklist-backup.json`).
 *
 * The schema is versioned through [schemaVersion]: restoring a file with an unknown
 * version must be refused (see `BackupFileSerializer`).
 *
 * All dates are stored as epoch millis — the persisted format used by Room
 * (`Converters` converts `Calendar` <-> `Long`).
 */
@Serializable
data class BackupFileModel(
    val schemaVersion: Int = CURRENT_SCHEMA_VERSION,
    val createdAt: Long,
    val checklists: List<ChecklistBackupModel>,
) {
    companion object {
        const val CURRENT_SCHEMA_VERSION = 1
    }
}

/**
 * Mirrors `new_checklist` column-for-column. Sections (self foreign key through
 * [parentUuid]) are stored as flat rows exactly like Room does.
 */
@Serializable
data class ChecklistBackupModel(
    val uuid: String,
    val parentUuid: String?,
    val name: String,
    val isSelected: Boolean,
    val createdDate: Long,
    val lastUpdate: Long,
    val tasks: List<TaskBackupModel>,
)

/**
 * Mirrors `new_task` column-for-column.
 */
@Serializable
data class TaskBackupModel(
    val uuid: String,
    val parentUuid: String,
    val name: String,
    val isCompleted: Boolean,
    val dateCreated: Long,
)

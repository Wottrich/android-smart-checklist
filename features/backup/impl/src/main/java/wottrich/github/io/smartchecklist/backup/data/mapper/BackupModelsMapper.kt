package wottrich.github.io.smartchecklist.backup.data.mapper

import wottrich.github.io.smartchecklist.backup.data.backupfile.BackupFileModel
import wottrich.github.io.smartchecklist.backup.data.backupfile.ChecklistBackupModel
import wottrich.github.io.smartchecklist.backup.data.backupfile.TaskBackupModel
import wottrich.github.io.smartchecklist.datasource.data.model.Checklist
import wottrich.github.io.smartchecklist.datasource.data.model.ChecklistWithTasks
import wottrich.github.io.smartchecklist.datasource.data.model.Task

/**
 * Layer-boundary mappers between the Room-facing models (`:datasource:public`) and
 * the serialized backup file schema (`:features:backup:public`).
 *
 * Checklists are stored flat: sections are regular checklist rows whose
 * `parentUuid` points to their parent, exactly like the Room tables do.
 */
internal fun ChecklistWithTasks.toBackupModel(): ChecklistBackupModel = ChecklistBackupModel(
    uuid = checklist.uuid,
    parentUuid = checklist.parentUuid,
    name = checklist.name,
    isSelected = checklist.isSelected,
    createdDate = checklist.createdDate,
    lastUpdate = checklist.lastUpdate,
    tasks = tasks.map { it.toBackupModel() }
)

internal fun Task.toBackupModel(): TaskBackupModel = TaskBackupModel(
    uuid = uuid,
    parentUuid = parentUuid,
    name = name,
    isCompleted = isCompleted,
    dateCreated = dateCreated
)

internal fun ChecklistBackupModel.toChecklistWithTasks(): ChecklistWithTasks = ChecklistWithTasks(
    checklist = Checklist(
        uuid = uuid,
        parentUuid = parentUuid,
        name = name,
        isSelected = isSelected,
        createdDate = createdDate,
        lastUpdate = lastUpdate
    ),
    tasks = tasks.map { it.toTask() },
    checklistSectionEmbedded = listOf()
)

internal fun TaskBackupModel.toTask(): Task = Task(
    uuid = uuid,
    parentUuid = parentUuid,
    name = name,
    isCompleted = isCompleted,
    dateCreated = dateCreated
)

internal fun BackupFileModel.toChecklistsWithTasks(): List<ChecklistWithTasks> =
    checklists.map { it.toChecklistWithTasks() }

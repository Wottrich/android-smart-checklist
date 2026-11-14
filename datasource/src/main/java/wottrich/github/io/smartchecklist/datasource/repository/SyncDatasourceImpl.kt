package wottrich.github.io.smartchecklist.datasource.repository

import androidx.room.withTransaction
import wottrich.github.io.smartchecklist.datasource.AppDatabase
import wottrich.github.io.smartchecklist.datasource.dao.ChecklistDao
import wottrich.github.io.smartchecklist.datasource.dao.TaskDao
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncDatasource
import wottrich.github.io.smartchecklist.datasource.data.model.Checklist
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.datasource.entity.ChecklistDTO
import wottrich.github.io.smartchecklist.datasource.entity.TaskDTO
import java.util.Calendar

/**
 * Row-level surface for the server-sync engine. Upserts deliberately avoid SQL
 * `REPLACE`: replace deletes the conflicting row first, and the checklist foreign
 * keys are `ON DELETE CASCADE` — that would silently wipe a checklist's sections
 * and tasks on every pulled upsert. Insert-IGNORE-then-update keeps children intact.
 */
class SyncDatasourceImpl(
    private val database: AppDatabase,
    private val checklistDao: ChecklistDao,
    private val taskDao: TaskDao
) : SyncDatasource {

    override suspend fun getChecklistRow(uuid: String): Checklist? {
        return checklistDao.findChecklist(uuid)?.mapToRow()
    }

    override suspend fun getTaskRow(uuid: String): Task? {
        return taskDao.findTask(uuid)?.mapToRow()
    }

    override suspend fun applyPage(
        upsertChecklists: List<Checklist>,
        upsertTasks: List<Task>,
        deleteChecklistUuids: List<String>,
        deleteTaskUuids: List<String>
    ) {
        database.withTransaction {
            upsertChecklists
                // parents first, so the self foreign key never fails mid-page
                .sortedBy { it.parentUuid != null }
                .forEach { row ->
                    val dto = row.mapToDTO()
                    if (checklistDao.insert(dto) == INSERT_CONFLICT) {
                        checklistDao.update(dto)
                    }
                }
            upsertTasks.forEach { row ->
                val dto = row.mapToDTO()
                if (taskDao.insert(dto) == INSERT_CONFLICT) {
                    taskDao.update(dto)
                }
            }
            deleteChecklistUuids.forEach { checklistDao.deleteByUuid(it) }
            deleteTaskUuids.forEach { taskDao.deleteByUuid(it) }
        }
    }

    override suspend fun deleteChecklistByUuid(uuid: String) {
        checklistDao.deleteByUuid(uuid)
    }

    override suspend fun deleteTaskByUuid(uuid: String) {
        taskDao.deleteByUuid(uuid)
    }

    private companion object {
        /** `OnConflictStrategy.IGNORE` returns -1 when the row already exists. */
        const val INSERT_CONFLICT = -1L
    }
}

private fun ChecklistDTO.mapToRow() = Checklist(
    uuid = uuid,
    parentUuid = parentUuid,
    name = name,
    isSelected = isSelected,
    createdDate = createdDate.timeInMillis,
    lastUpdate = lastUpdate.timeInMillis,
)

private fun TaskDTO.mapToRow() = Task(
    uuid = uuid,
    parentUuid = parentUuid,
    name = name,
    isCompleted = isCompleted,
    dateCreated = dateCreated.timeInMillis,
)

private fun Checklist.mapToDTO() = ChecklistDTO(
    uuid = uuid,
    parentUuid = parentUuid,
    name = name,
    isSelected = isSelected,
    createdDate = createdDate.toCalendar(),
    lastUpdate = lastUpdate.toCalendar(),
)

private fun Task.mapToDTO() = TaskDTO(
    uuid = uuid,
    parentUuid = parentUuid,
    name = name,
    isCompleted = isCompleted,
    dateCreated = dateCreated.toCalendar(),
)

private fun Long.toCalendar(): Calendar =
    Calendar.getInstance().apply { timeInMillis = this@toCalendar }

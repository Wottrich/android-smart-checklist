package wottrich.github.io.smartchecklist.data.repository

import wottrich.github.io.smartchecklist.datasource.data.datasource.TaskDatasource
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.domain.repository.TaskRepository
import wottrich.github.io.smartchecklist.serversync.api.SyncOutbox

class TaskRepositoryImpl(
    private val taskDatasource: TaskDatasource,
    private val syncOutbox: SyncOutbox
) : TaskRepository {
    override suspend fun getTasksByChecklistUuid(checklistUuid: String): List<Task> {
        return taskDatasource.getTasksByChecklistUuid(checklistUuid)
    }

    /**
     * Server-sync (Phase E): every user mutation lands in Room first, then queues an
     * outbox entry with the final row state. No-ops when the server is not configured.
     */
    override suspend fun insertTask(task: Task): Long? {
        val rowId = taskDatasource.insertTask(task)
        if (rowId != null && rowId > 0) syncOutbox.onTaskUpserted(task.uuid)
        return rowId
    }

    override suspend fun updateTask(task: Task) {
        taskDatasource.updateTask(task)
        syncOutbox.onTaskUpserted(task.uuid)
    }

    override suspend fun deleteTask(task: Task) {
        taskDatasource.deleteTask(task)
        syncOutbox.onTaskDeleted(task.uuid)
    }
}
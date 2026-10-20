package wottrich.github.io.smartchecklist.data.repository

import wottrich.github.io.smartchecklist.datasource.data.datasource.TaskDatasource
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.domain.repository.TaskRepository

class TaskRepositoryImpl(
    private val taskDatasource: TaskDatasource
) : TaskRepository {
    override suspend fun getTasksByChecklistUuid(checklistUuid: String): List<Task> {
        return taskDatasource.getTasksByChecklistUuid(checklistUuid)
    }

    override suspend fun insertTask(task: Task): Long? {
        return taskDatasource.insertTask(task)
    }

    override suspend fun updateTask(task: Task) {
        taskDatasource.updateTask(task)
    }

    override suspend fun deleteTask(task: Task) {
        taskDatasource.deleteTask(task)
    }
}
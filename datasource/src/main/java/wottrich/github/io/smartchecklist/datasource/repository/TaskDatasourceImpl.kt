package wottrich.github.io.smartchecklist.datasource.repository

import wottrich.github.io.smartchecklist.datasource.dao.TaskDao
import wottrich.github.io.smartchecklist.datasource.data.datasource.TaskDatasource
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.datasource.entity.TaskDTO

class TaskDatasourceImpl(
    private val taskDao: TaskDao
) : TaskDatasource {
    override suspend fun getTasksByChecklistUuid(checklistUuid: String): List<Task> {
        return taskDao.getTasks(checklistUuid).map { it.mapToTask() }
    }

    override suspend fun insertTask(task: Task): Long? {
        return taskDao.insert(task.mapToDTO())
    }

    override suspend fun insertManyTasks(tasks: List<Task>) {
        return taskDao.insertMany(tasks.map { it.mapToDTO() })
    }

    override suspend fun updateTask(task: Task) {
        taskDao.update(task.mapToDTO())
    }

    override suspend fun updateTasks(tasks: List<Task>) {
        taskDao.updateTasks(tasks.map { it.mapToDTO() })
    }

    override suspend fun deleteTask(task: Task) {
        taskDao.delete(task.mapToDTO())
    }

    private fun Task.mapToDTO() = TaskDTO(
        this.uuid,
        this.parentUuid,
        this.name,
        this.isCompleted
    )

    private fun TaskDTO.mapToTask() = Task(
        this.uuid,
        this.parentUuid,
        this.name,
        this.isCompleted
    )
}
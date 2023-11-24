package wottrich.github.io.smartchecklist.data.repository

import wottrich.github.io.smartchecklist.datasource.dao.ChecklistDao
import wottrich.github.io.smartchecklist.datasource.dao.TaskDao
import wottrich.github.io.smartchecklist.datasource.entity.NewTask
import wottrich.github.io.smartchecklist.domain.repository.TaskRepository
import java.security.spec.InvalidParameterSpecException

class TaskRepositoryImpl(
    private val taskDao: TaskDao,
    private val checklistDao: ChecklistDao
) : TaskRepository {
    override suspend fun getTasksFromSelectedChecklist(): List<NewTask> {
        val selectedChecklist =
            checklistDao.getSelectedChecklist() ?: throw InvalidParameterSpecException()
        return taskDao.getTasks(selectedChecklist.uuid)
    }
}
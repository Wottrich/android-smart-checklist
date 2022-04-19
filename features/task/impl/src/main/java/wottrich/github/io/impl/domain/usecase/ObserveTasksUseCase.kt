package wottrich.github.io.impl.domain.usecase

import kotlinx.coroutines.flow.Flow
import wottrich.github.io.datasource.dao.TaskDao
import wottrich.github.io.datasource.entity.Task

class ObserveTasksUseCase(private val taskDao: TaskDao) {
    operator fun invoke(checklistId: String): Flow<List<Task>> {
        return taskDao.observeTasksUpdate(checklistId)
    }
}
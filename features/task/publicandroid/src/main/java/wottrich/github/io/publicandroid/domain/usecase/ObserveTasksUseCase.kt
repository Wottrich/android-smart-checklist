package wottrich.github.io.publicandroid.domain.usecase

import kotlinx.coroutines.flow.Flow
import wottrich.github.io.database.dao.TaskDao
import wottrich.github.io.database.entity.Task

class ObserveTasksUseCase(private val taskDao: TaskDao) {
    operator fun invoke(checklistId: String): Flow<List<Task>> {
        return taskDao.observeTasksUpdate(checklistId)
    }
}
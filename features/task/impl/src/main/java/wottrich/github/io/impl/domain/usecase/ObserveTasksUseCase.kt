package wottrich.github.io.impl.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import wottrich.github.io.datasource.dao.TaskDao
import wottrich.github.io.datasource.entity.Task
import wottrich.github.io.tools.base.FlowableUseCase
import wottrich.github.io.tools.base.Result

class ObserveTasksUseCase(private val taskDao: TaskDao) : FlowableUseCase<String, List<Task>>() {
    override suspend fun execute(params: String): Flow<Result<List<Task>>> {
        return taskDao.observeTasksUpdate(params).map { Result.success(it) }
    }
}
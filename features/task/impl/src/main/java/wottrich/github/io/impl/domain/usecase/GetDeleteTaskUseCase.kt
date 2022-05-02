package wottrich.github.io.impl.domain.usecase

import wottrich.github.io.datasource.dao.TaskDao
import wottrich.github.io.datasource.entity.Task
import wottrich.github.io.tools.base.KotlinResultUseCase
import wottrich.github.io.tools.base.UseCase
import wottrich.github.io.tools.base.UseCase.Empty
import wottrich.github.io.tools.base.successEmptyResult

class GetDeleteTaskUseCase(private val taskDao: TaskDao) : KotlinResultUseCase<Task, UseCase.Empty>() {
    override suspend fun execute(params: Task): Result<Empty> {
        taskDao.delete(params)
        return successEmptyResult()
    }
}

package wottrich.github.io.impl.domain.usecase

import wottrich.github.io.datasource.dao.TaskDao
import wottrich.github.io.datasource.entity.NewTask
import wottrich.github.io.tools.base.KotlinResultUseCase
import wottrich.github.io.tools.base.Result
import wottrich.github.io.tools.base.UseCase
import wottrich.github.io.tools.base.UseCase.Empty
import wottrich.github.io.tools.base.successEmptyResult

class AddManyTasksUseCase(
    private val taskDao: TaskDao
) : KotlinResultUseCase<List<NewTask>, UseCase.Empty>() {
    override suspend fun execute(params: List<NewTask>): Result<Empty> {
        taskDao.insertMany(params)
        return successEmptyResult()
    }
}
package github.io.wottrich.checklist.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import wottrich.github.io.datasource.dao.ChecklistDao
import wottrich.github.io.datasource.entity.ChecklistWithTasks
import wottrich.github.io.tools.base.FlowableUseCase
import wottrich.github.io.tools.base.Result
import wottrich.github.io.tools.base.UseCase

class GetChecklistWithTaskUseCase(private val checklistDao: ChecklistDao) :
    FlowableUseCase<UseCase.None, List<ChecklistWithTasks>>() {
    override suspend fun execute(params: UseCase.None): Flow<Result<List<ChecklistWithTasks>>> {
        return checklistDao.observeChecklistsWithTaskUpdate().map { Result.success(it) }
    }
}
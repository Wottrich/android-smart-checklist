package github.io.wottrich.checklist.domain.usecase

import github.io.wottrich.coroutines.FlowableUseCase
import github.io.wottrich.coroutines.UseCase
import github.io.wottrich.coroutines.base.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import wottrich.github.io.datasource.dao.ChecklistDao
import wottrich.github.io.datasource.entity.NewChecklistWithNewTasks

class GetChecklistWithTaskUseCase(private val checklistDao: ChecklistDao) :
    FlowableUseCase<UseCase.None, List<NewChecklistWithNewTasks>>() {
    override suspend fun execute(params: UseCase.None): Flow<Result<List<NewChecklistWithNewTasks>>> {
        return checklistDao.observeChecklistsWithTaskUpdate().map { Result.success(it) }
    }
}
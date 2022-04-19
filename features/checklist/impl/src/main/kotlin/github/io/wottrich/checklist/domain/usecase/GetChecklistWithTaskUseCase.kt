package github.io.wottrich.checklist.domain.usecase

import kotlinx.coroutines.flow.Flow
import wottrich.github.io.datasource.dao.ChecklistDao
import wottrich.github.io.datasource.entity.ChecklistWithTasks

class GetChecklistWithTaskUseCase(private val checklistDao: ChecklistDao) {
    operator fun invoke(): Flow<List<ChecklistWithTasks>> {
        return checklistDao.observeChecklistsWithTaskUpdate()
    }
}
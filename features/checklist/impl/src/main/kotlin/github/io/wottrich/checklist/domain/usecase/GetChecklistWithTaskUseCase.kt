package github.io.wottrich.checklist.domain.usecase

import kotlinx.coroutines.flow.Flow
import wottrich.github.io.database.dao.ChecklistDao
import wottrich.github.io.database.entity.ChecklistWithTasks

class GetChecklistWithTaskUseCase(private val checklistDao: ChecklistDao) {
    operator fun invoke(): Flow<List<ChecklistWithTasks>> {
        return checklistDao.observeChecklistsWithTaskUpdate()
    }
}
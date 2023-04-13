package wottrich.github.io.datasource.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapNotNull
import wottrich.github.io.datasource.dao.ChecklistDao
import wottrich.github.io.datasource.entity.NewChecklistWithNewTasks

class ChecklistRepositoryImpl(
    private val checklistDao: ChecklistDao
) : ChecklistRepository {
    override fun observeSelectedChecklistsWithTask(): Flow<NewChecklistWithNewTasks> {
        return checklistDao.observeSelectedChecklistWithTasks().mapNotNull {
            it.getOrSaveSelectedChecklist()
        }.distinctUntilChanged()
    }

    private suspend fun List<NewChecklistWithNewTasks>.getOrSaveSelectedChecklist(): NewChecklistWithNewTasks? {
        return firstOrNull() ?: getFirstChecklist()?.also {
            val checklist = it.newChecklist
            checklist.isSelected = true
            checklistDao.update(checklist)
        }
    }

    private suspend fun getFirstChecklist(): NewChecklistWithNewTasks? {
        return checklistDao.selectAllChecklistWithTasks().firstOrNull()
    }

}
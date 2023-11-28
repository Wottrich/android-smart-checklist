package wottrich.github.io.smartchecklist.datasource.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import wottrich.github.io.smartchecklist.datasource.dao.ChecklistDao
import wottrich.github.io.smartchecklist.datasource.entity.NewChecklistWithNewTasks

class ChecklistRepositoryImpl(
    private val checklistDao: ChecklistDao
) : ChecklistRepository {
    override suspend fun getChecklistWithTasksByUuid(uuid: String): NewChecklistWithNewTasks {
        return checklistDao.getChecklistWithTasks(uuid)
    }

    override fun observeSelectedChecklistWithTasks(): Flow<NewChecklistWithNewTasks?> {
        return checklistDao.observeSelectedChecklistWithTasks().map {
            it.getOrSaveSelectedChecklist()
        }
    }

    private suspend fun List<NewChecklistWithNewTasks>.getOrSaveSelectedChecklist(): NewChecklistWithNewTasks? {
        return firstOrNull() ?: getFirstChecklist()?.also {
            val checklist = it.newChecklist.copy(isSelected = true)
            checklistDao.update(checklist)
        }
    }

    private suspend fun getFirstChecklist(): NewChecklistWithNewTasks? {
        return checklistDao.selectAllChecklistWithTasks().firstOrNull()
    }

    override fun observeAllChecklistsWithTask(): Flow<List<NewChecklistWithNewTasks>> {
        return checklistDao.observeAllChecklistWithTasks()
    }

    override suspend fun updateSelectedChecklist(checklistUuid: String) {
        val checklistToBeSelected = checklistDao.getChecklist(checklistUuid).copy(isSelected = true)
        val currentSelectedChecklist = checklistDao.selectSelectedChecklist()?.copy(isSelected = false)
        if (currentSelectedChecklist == null) {
            checklistDao.update(checklistToBeSelected)
        } else {
            checklistDao.updateChecklists(listOf(currentSelectedChecklist, checklistToBeSelected))
        }
    }

    override suspend fun deleteChecklistByUuid(checklistUuid: String) {
        checklistDao.deleteChecklistByUuid(checklistUuid)
    }
}
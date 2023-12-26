package wottrich.github.io.smartchecklist.datasource.repository

import kotlinx.coroutines.flow.Flow
import wottrich.github.io.smartchecklist.datasource.entity.NewChecklist
import wottrich.github.io.smartchecklist.datasource.entity.NewChecklistWithNewTasks

interface ChecklistRepository {
    suspend fun getChecklistWithTasksByUuid(uuid: String): NewChecklistWithNewTasks
    fun observeSelectedChecklistWithTasks(): Flow<NewChecklistWithNewTasks?>
    suspend fun getSelectedChecklistWithTasks(): NewChecklistWithNewTasks?
    fun observeAllChecklistsWithTask(): Flow<List<NewChecklistWithNewTasks>>
    fun observeSelectedChecklist(): Flow<NewChecklist?>
    suspend fun updateSelectedChecklist(checklistUuid: String)
    suspend fun deleteChecklistByUuid(checklistUuid: String)
    fun observeSelectedChecklistUuid(): Flow<String?>
}
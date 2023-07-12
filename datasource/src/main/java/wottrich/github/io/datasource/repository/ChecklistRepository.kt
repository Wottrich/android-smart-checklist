package wottrich.github.io.datasource.repository

import kotlinx.coroutines.flow.Flow
import wottrich.github.io.datasource.entity.NewChecklistWithNewTasks

interface ChecklistRepository {
    suspend fun getChecklistWithTasksByUuid(uuid: String) : NewChecklistWithNewTasks
    fun observeSelectedChecklistWithTasks(): Flow<NewChecklistWithNewTasks?>
    fun observeAllChecklistsWithTask(): Flow<List<NewChecklistWithNewTasks>>
    suspend fun updateSelectedChecklist(checklistUuid: String)
    suspend fun deleteChecklistByUuid(checklistUuid: String)
}
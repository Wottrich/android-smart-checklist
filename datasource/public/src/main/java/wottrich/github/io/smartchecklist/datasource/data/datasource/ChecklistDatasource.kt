package wottrich.github.io.smartchecklist.datasource.data.datasource

import kotlinx.coroutines.flow.Flow
import wottrich.github.io.smartchecklist.datasource.data.model.Checklist
import wottrich.github.io.smartchecklist.datasource.data.model.ChecklistWithTasks

interface ChecklistDatasource {
    suspend fun insertChecklist(checklist: Checklist): Long?
    suspend fun getChecklistWithTasksByUuid(uuid: String): ChecklistWithTasks
    suspend fun getSelectedChecklistWithTasks(): ChecklistWithTasks?
    suspend fun updateSelectedChecklist(checklistUuid: String)
    suspend fun deleteChecklistByUuid(checklistUuid: String)
    fun observeAllChecklistsWithTask(): Flow<List<ChecklistWithTasks>>
    fun observeSelectedChecklist(): Flow<Checklist?>
    fun observeSelectedChecklistUuid(): Flow<String?>
}
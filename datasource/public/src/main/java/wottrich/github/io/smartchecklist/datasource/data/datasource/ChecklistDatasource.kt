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
    suspend fun getSelectedChecklist(): Checklist?
    fun observeSelectedChecklistWithTasks(): Flow<ChecklistWithTasks?>
    fun observeAllChecklistsWithTask(): Flow<List<ChecklistWithTasks>>
    fun observeSelectedChecklist(): Flow<Checklist?>
    fun observeSelectedChecklistUuid(): Flow<String?>

    /**
     * Every checklist (roots and sections) with its direct tasks — used by the
     * Google Drive backup (issue #94).
     */
    suspend fun getAllChecklistsWithTasks(): List<ChecklistWithTasks>

    /**
     * Full-replace of all checklists and tasks in a single transaction — used by
     * the Google Drive backup restore (issue #94). Selection is normalized to at
     * most one selected checklist.
     */
    suspend fun replaceAllChecklists(checklists: List<ChecklistWithTasks>)
}

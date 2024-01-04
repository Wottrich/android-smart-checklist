package wottrich.github.io.smartchecklist.checklist.data.repository

import kotlinx.coroutines.flow.Flow
import wottrich.github.io.smartchecklist.datasource.data.datasource.ChecklistDatasource
import wottrich.github.io.smartchecklist.datasource.data.model.Checklist
import wottrich.github.io.smartchecklist.datasource.data.model.ChecklistWithTasks

class ChecklistRepositoryImpl(
    private val datasource: ChecklistDatasource
) : ChecklistRepository {
    override suspend fun insertChecklist(checklist: Checklist): Long? {
        return datasource.insertChecklist(checklist)
    }

    override suspend fun getChecklistWithTasksByUuid(uuid: String): ChecklistWithTasks {
        return datasource.getChecklistWithTasksByUuid(uuid)
    }

    override suspend fun getSelectedChecklistWithTasks(): ChecklistWithTasks? {
        return datasource.getSelectedChecklistWithTasks()
    }

    override suspend fun updateSelectedChecklist(checklistUuid: String) {
        return datasource.updateSelectedChecklist(checklistUuid)
    }

    override suspend fun deleteChecklistByUuid(checklistUuid: String) {
        return datasource.deleteChecklistByUuid(checklistUuid)
    }

    override fun observeSelectedChecklistWithTasks(): Flow<ChecklistWithTasks?> {
        return datasource.observeSelectedChecklistWithTasks()
    }

    override fun observeAllChecklistsWithTask(): Flow<List<ChecklistWithTasks>> {
        return datasource.observeAllChecklistsWithTask()
    }

    override fun observeSelectedChecklist(): Flow<Checklist?> {
        return datasource.observeSelectedChecklist()
    }

    override fun observeSelectedChecklistUuid(): Flow<String?> {
        return datasource.observeSelectedChecklistUuid()
    }

}
package wottrich.github.io.smartchecklist.checklist.data.repository

import kotlinx.coroutines.flow.Flow
import wottrich.github.io.smartchecklist.datasource.data.datasource.ChecklistDatasource
import wottrich.github.io.smartchecklist.datasource.data.model.Checklist
import wottrich.github.io.smartchecklist.datasource.data.model.ChecklistWithTasks
import wottrich.github.io.smartchecklist.serversync.api.SyncOutbox

class ChecklistRepositoryImpl(
    private val datasource: ChecklistDatasource,
    private val syncOutbox: SyncOutbox
) : ChecklistRepository {

    /**
     * Server-sync (Phase E): every user mutation lands in Room first, then queues an
     * outbox entry with the final row state. No-ops when the server is not configured.
     */
    override suspend fun insertChecklist(checklist: Checklist): Long? {
        val rowId = datasource.insertChecklist(checklist)
        if (rowId != null) syncOutbox.onChecklistUpserted(checklist.uuid)
        return rowId
    }

    override suspend fun getChecklistWithTasksByUuid(uuid: String): ChecklistWithTasks {
        return datasource.getChecklistWithTasksByUuid(uuid)
    }

    override suspend fun getSelectedChecklistWithTasks(): ChecklistWithTasks? {
        return datasource.getSelectedChecklistWithTasks()
    }

    override suspend fun getSelectedChecklist(): Checklist? {
        return datasource.getSelectedChecklist()
    }

    override suspend fun updateSelectedChecklist(checklistUuid: String) {
        datasource.updateSelectedChecklist(checklistUuid)
        syncOutbox.onChecklistUpserted(checklistUuid)
    }

    override suspend fun deleteChecklistByUuid(checklistUuid: String) {
        datasource.deleteChecklistByUuid(checklistUuid)
        syncOutbox.onChecklistDeleted(checklistUuid)
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
package wottrich.github.io.smartchecklist.datasource.repository

import wottrich.github.io.smartchecklist.datasource.dao.SyncOutboxDao
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncOutboxDatasource
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncOutboxEntry
import wottrich.github.io.smartchecklist.datasource.entity.SyncOutboxDTO

class SyncOutboxDatasourceImpl(
    private val dao: SyncOutboxDao
) : SyncOutboxDatasource {

    override suspend fun enqueue(entry: SyncOutboxEntry) {
        dao.insert(entry.mapToDTO())
    }

    override suspend fun pending(limit: Int): List<SyncOutboxEntry> {
        return dao.pending(limit).map { it.mapToEntry() }
    }

    override suspend fun deleteByIds(ids: List<Long>) {
        if (ids.isNotEmpty()) dao.deleteByIds(ids)
    }

    override suspend fun clearAll() {
        dao.deleteAll()
    }

    override suspend fun count(): Int = dao.count()
}

private fun SyncOutboxEntry.mapToDTO() = SyncOutboxDTO(
    id = id,
    entity = entity,
    entityUuid = entityUuid,
    op = op,
    at = at,
    payload = payload,
)

private fun SyncOutboxDTO.mapToEntry() = SyncOutboxEntry(
    id = id,
    entity = entity,
    entityUuid = entityUuid,
    op = op,
    at = at,
    payload = payload,
)

package wottrich.github.io.smartchecklist.datasource.repository

import wottrich.github.io.smartchecklist.datasource.dao.SharedChecklistMetaDao
import wottrich.github.io.smartchecklist.datasource.data.datasource.SharedChecklistMeta
import wottrich.github.io.smartchecklist.datasource.data.datasource.SharedChecklistMetaDatasource
import wottrich.github.io.smartchecklist.datasource.entity.SharedChecklistMetaDTO

class SharedChecklistMetaDatasourceImpl(
    private val dao: SharedChecklistMetaDao
) : SharedChecklistMetaDatasource {

    override suspend fun getByChecklistUuid(checklistUuid: String): SharedChecklistMeta? {
        return dao.getByChecklistUuid(checklistUuid)?.mapToModel()
    }

    override suspend fun getAll(): List<SharedChecklistMeta> {
        return dao.getAll().map { it.mapToModel() }
    }

    override suspend fun getDistinctActiveHostIds(): List<String> {
        return dao.getDistinctActiveHostIds()
    }

    override suspend fun upsert(meta: SharedChecklistMeta) {
        dao.upsert(meta.mapToDTO())
    }

    override suspend fun setCursor(checklistUuid: String, cursor: Long) {
        dao.setCursor(checklistUuid, cursor)
    }

    override suspend fun setDetached(checklistUuid: String, detached: Boolean) {
        dao.setDetached(checklistUuid, detached)
    }

    override suspend fun setLastSyncAt(checklistUuid: String, at: Long?) {
        dao.setLastSyncAt(checklistUuid, at)
    }

    override suspend fun deleteByChecklistUuid(checklistUuid: String) {
        dao.deleteByChecklistUuid(checklistUuid)
    }
}

private fun SharedChecklistMetaDTO.mapToModel() = SharedChecklistMeta(
    checklistUuid = checklistUuid,
    hostDeviceId = hostDeviceId,
    hostAddress = hostAddress,
    shareId = shareId,
    hostToken = hostToken,
    cursor = cursor,
    isOwner = isOwner,
    detached = detached,
    lastSyncAt = lastSyncAt,
)

private fun SharedChecklistMeta.mapToDTO() = SharedChecklistMetaDTO(
    checklistUuid = checklistUuid,
    hostDeviceId = hostDeviceId,
    hostAddress = hostAddress,
    shareId = shareId,
    hostToken = hostToken,
    cursor = cursor,
    isOwner = isOwner,
    detached = detached,
    lastSyncAt = lastSyncAt,
)

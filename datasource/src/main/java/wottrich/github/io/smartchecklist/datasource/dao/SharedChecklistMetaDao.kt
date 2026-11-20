package wottrich.github.io.smartchecklist.datasource.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import wottrich.github.io.smartchecklist.datasource.entity.SharedChecklistMetaDTO

@Dao
interface SharedChecklistMetaDao {

    @Upsert
    suspend fun upsert(dto: SharedChecklistMetaDTO)

    @Query("SELECT * FROM shared_checklist_meta WHERE checklist_uuid = :checklistUuid")
    suspend fun getByChecklistUuid(checklistUuid: String): SharedChecklistMetaDTO?

    @Query("SELECT * FROM shared_checklist_meta")
    suspend fun getAll(): List<SharedChecklistMetaDTO>

    /** Distinct hosts that still have at least one active (non-detached) share. */
    @Query("SELECT DISTINCT host_device_id FROM shared_checklist_meta WHERE detached = 0")
    suspend fun getDistinctActiveHostIds(): List<String>

    @Query("UPDATE shared_checklist_meta SET `cursor` = :cursor WHERE checklist_uuid = :checklistUuid")
    suspend fun setCursor(checklistUuid: String, cursor: Long)

    @Query("UPDATE shared_checklist_meta SET detached = :detached WHERE checklist_uuid = :checklistUuid")
    suspend fun setDetached(checklistUuid: String, detached: Boolean)

    @Query("UPDATE shared_checklist_meta SET last_sync_at = :at WHERE checklist_uuid = :checklistUuid")
    suspend fun setLastSyncAt(checklistUuid: String, at: Long?)

    @Query("DELETE FROM shared_checklist_meta WHERE checklist_uuid = :checklistUuid")
    suspend fun deleteByChecklistUuid(checklistUuid: String)
}

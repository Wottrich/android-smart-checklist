package wottrich.github.io.smartchecklist.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import wottrich.github.io.smartchecklist.datasource.entity.SyncOutboxDTO

@Dao
interface SyncOutboxDao {

    @Insert
    suspend fun insert(dto: SyncOutboxDTO)

    /**
     * Oldest first — push order must match mutation order. Host-scoped: each host
     * (PC backend = `null`, device share host = id) is drained by its own session.
     */
    @Query("SELECT * FROM sync_outbox WHERE host_id IS :hostId ORDER BY id ASC LIMIT :limit")
    suspend fun pending(hostId: String?, limit: Int): List<SyncOutboxDTO>

    @Query("DELETE FROM sync_outbox WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    @Query("DELETE FROM sync_outbox")
    suspend fun deleteAll()

    /** Pending count for one host (`null` = PC backend). */
    @Query("SELECT COUNT(*) FROM sync_outbox WHERE host_id IS :hostId")
    suspend fun count(hostId: String?): Int
}

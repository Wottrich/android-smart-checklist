package wottrich.github.io.smartchecklist.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import wottrich.github.io.smartchecklist.datasource.entity.SyncOutboxDTO

@Dao
interface SyncOutboxDao {

    @Insert
    suspend fun insert(dto: SyncOutboxDTO)

    /** Oldest first — push order must match mutation order. */
    @Query("SELECT * FROM sync_outbox ORDER BY id ASC LIMIT :limit")
    suspend fun pending(limit: Int): List<SyncOutboxDTO>

    @Query("DELETE FROM sync_outbox WHERE id IN (:ids)")
    suspend fun deleteByIds(ids: List<Long>)

    @Query("DELETE FROM sync_outbox")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM sync_outbox")
    suspend fun count(): Int
}

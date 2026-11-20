package wottrich.github.io.smartchecklist.share.host.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ShareDao {

    // ---------- shares / guests / members ----------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShare(share: ShareDTO)

    @Query("SELECT * FROM shares WHERE active = 1")
    suspend fun getActiveShares(): List<ShareDTO>

    @Query("SELECT * FROM shares WHERE share_id = :shareId")
    suspend fun getShare(shareId: String): ShareDTO?

    @Query("UPDATE shares SET active = 0 WHERE share_id = :shareId")
    suspend fun deactivateShare(shareId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGuest(guest: ShareGuestDTO)

    @Query("SELECT * FROM guests WHERE token_hash = :tokenHash LIMIT 1")
    suspend fun findGuestByTokenHash(tokenHash: String): ShareGuestDTO?

    /** Sliding expiry: extend on authenticated activity. */
    @Query("UPDATE guests SET last_seen_at = :now, expires_at = :expiresAt WHERE user_id = :userId")
    suspend fun touchGuest(userId: String, now: Long, expiresAt: Long)

    /** "Reset pairing": every guest must re-pair (soft revocation). */
    @Query("UPDATE guests SET revoked = 1 WHERE revoked = 0")
    suspend fun revokeAllGuests()

    /** Purge guests revoked/expired more than [olderThan] millis ago. */
    @Query("DELETE FROM guests WHERE revoked = 1 AND last_seen_at < :olderThan")
    suspend fun purgeStaleGuests(olderThan: Long)

    @Query("SELECT COUNT(*) FROM share_members WHERE share_id = :shareId")
    suspend fun countMembers(shareId: String): Int

    @Query("SELECT EXISTS(SELECT 1 FROM share_members WHERE share_id = :shareId AND user_id = :userId)")
    suspend fun isMember(shareId: String, userId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMember(member: ShareMemberDTO)

    @Query("SELECT DISTINCT share_id FROM share_members WHERE user_id = :userId")
    suspend fun getMemberShareIds(userId: String): List<String>

    // ---------- change log ----------

    @Insert
    suspend fun insertChange(change: ShareChangeLogDTO)

    @Query(
        "SELECT * FROM change_log WHERE share_id = :shareId AND server_seq > :since " +
            "ORDER BY server_seq ASC LIMIT :limit"
    )
    suspend fun pullPage(shareId: String, since: Long, limit: Int): List<ShareChangeLogDTO>

    @Query("SELECT COALESCE(MAX(server_seq), 0) FROM change_log WHERE share_id = :shareId")
    suspend fun maxServerSeq(shareId: String): Long

    @Query("SELECT COALESCE(MAX(at), 0) FROM change_log WHERE share_id = :shareId")
    suspend fun lastChangeAt(shareId: String): Long

    // ---------- state mirror ----------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertChecklist(checklist: HostChecklistDTO)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTask(task: HostTaskDTO)

    @Query("SELECT * FROM host_checklists WHERE uuid = :uuid")
    suspend fun findChecklist(uuid: String): HostChecklistDTO?

    @Query("SELECT * FROM host_tasks WHERE uuid = :uuid")
    suspend fun findTask(uuid: String): HostTaskDTO?

    /** Direct children (sections) of a checklist, any share. */
    @Query("SELECT * FROM host_checklists WHERE parent_uuid = :parentUuid")
    suspend fun findChecklistChildren(parentUuid: String): List<HostChecklistDTO>

    @Query("SELECT * FROM host_tasks WHERE parent_uuid = :parentUuid")
    suspend fun findTasks(parentUuid: String): List<HostTaskDTO>

    @Query("DELETE FROM host_checklists WHERE uuid IN (:uuids)")
    suspend fun deleteChecklists(uuids: List<String>)

    @Query("DELETE FROM host_tasks WHERE uuid IN (:uuids)")
    suspend fun deleteTasks(uuids: List<String>)

    // ---------- tombstones ----------

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTombstone(tombstone: ShareTombstoneDTO)

    @Query("SELECT * FROM tombstones WHERE uuid = :uuid")
    suspend fun findTombstone(uuid: String): ShareTombstoneDTO?

    /**
     * Applies one change atomically: the change-log append is inseparable from its
     * mirror/tombstone write, so a crash can never publish a change whose state
     * was not committed (whole-batch atomicity is layered on top of this).
     */
    @Transaction
    suspend fun applyChange(
        change: ShareChangeLogDTO,
        checklist: HostChecklistDTO?,
        task: HostTaskDTO?,
        tombstone: ShareTombstoneDTO?,
        removeChecklistUuid: String?,
        removeTaskUuid: String?,
    ) {
        insertChange(change)
        checklist?.let { upsertChecklist(it) }
        task?.let { upsertTask(it) }
        tombstone?.let { insertTombstone(it) }
        removeChecklistUuid?.let { deleteChecklists(listOf(it)) }
        removeTaskUuid?.let { deleteTasks(listOf(it)) }
    }
}

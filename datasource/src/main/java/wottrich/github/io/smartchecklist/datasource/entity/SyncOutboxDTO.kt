package wottrich.github.io.smartchecklist.datasource.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Outbox of local mutations waiting to be pushed to the server-sync backend.
 * Insert-only until the server acks; no foreign keys on purpose (a deleted parent
 * must not cascade-erase its own tombstone before it is pushed).
 */
@Entity(tableName = "sync_outbox")
data class SyncOutboxDTO(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    /** `checklist` | `task` */
    @ColumnInfo(name = "entity")
    val entity: String,
    @ColumnInfo(name = "entity_uuid")
    val entityUuid: String,
    /** `upsert` | `delete` */
    @ColumnInfo(name = "op")
    val op: String,
    /** Device time of the mutation (not the enqueue/push time) — drives LWW. */
    @ColumnInfo(name = "at")
    val at: Long,
    /** JSON snapshot of the row, frozen at enqueue time. */
    @ColumnInfo(name = "payload")
    val payload: String,
)

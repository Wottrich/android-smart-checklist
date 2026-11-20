package wottrich.github.io.smartchecklist.share.host.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/** One shared checklist advertised by this device (the offer). */
@Entity(tableName = "shares")
data class ShareDTO(
    @PrimaryKey
    @ColumnInfo(name = "share_id")
    val shareId: String,
    /** Root checklist uuid (in the owner's main Room). */
    @ColumnInfo(name = "root_uuid")
    val rootUuid: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    /** 1 = advertised in offers + accepts allowed. */
    @ColumnInfo(name = "active")
    val active: Boolean = true,
    /**
     * Content-key version (v2 hardening): bumped on revoke/reset so members must
     * re-pair — consumed by the payload-encryption envelope (Phase S2).
     */
    @ColumnInfo(name = "key_version")
    val keyVersion: Long = 1,
)

/**
 * A **paired** device on this host (v2 hardening). Pairing is owner-approved via
 * the displayed pairing code — there is no self-service registration. The raw
 * token is never stored, only its SHA-256 hash; tokens expire after 30 days of
 * inactivity (sliding) and can be revoked ("Reset pairing").
 */
@Entity(tableName = "guests")
data class ShareGuestDTO(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val userId: String,
    /** SHA-256(raw token), hex — lookups compare hashes, never the token. */
    @ColumnInfo(name = "token_hash")
    val tokenHash: String,
    @ColumnInfo(name = "name")
    val name: String?,
    /** Pairing time = approval time (owner typed the code for this device). */
    @ColumnInfo(name = "approved_at")
    val approvedAt: Long,
    @ColumnInfo(name = "last_seen_at")
    val lastSeenAt: Long,
    /** Sliding: extended on every authenticated request. */
    @ColumnInfo(name = "expires_at")
    val expiresAt: Long,
    /** 1 = revoked — the guest detaches (frozen copy) on its next sync. */
    @ColumnInfo(name = "revoked")
    val revoked: Boolean = false,
)

/** Membership grant: one guest may pull/push exactly the accepted shares. */
@Entity(tableName = "share_members", primaryKeys = ["share_id", "user_id"])
data class ShareMemberDTO(
    @ColumnInfo(name = "share_id")
    val shareId: String,
    @ColumnInfo(name = "user_id")
    val userId: String,
    @ColumnInfo(name = "accepted_at")
    val acceptedAt: Long,
)

/**
 * Append-only host change log — the pull source for every client. [payload] keeps
 * the frozen wire JSON (share payloads never carry `isSelected`).
 */
@Entity(tableName = "change_log")
data class ShareChangeLogDTO(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "server_seq")
    val serverSeq: Long = 0,
    /** `checklist` | `task` */
    @ColumnInfo(name = "entity")
    val entity: String,
    @ColumnInfo(name = "uuid")
    val uuid: String,
    /** `upsert` | `delete` */
    @ColumnInfo(name = "op")
    val op: String,
    /** Winning client mutation time (LWW). */
    @ColumnInfo(name = "at")
    val at: Long,
    @ColumnInfo(name = "share_id")
    val shareId: String,
    @ColumnInfo(name = "payload")
    val payload: String,
)

/**
 * Current mirror of a shared checklist row (upsert state for LWW + parent walks).
 * Dates are epoch millis. `at` is the mutation time of the winning change.
 */
@Entity(tableName = "host_checklists")
data class HostChecklistDTO(
    @PrimaryKey
    @ColumnInfo(name = "uuid")
    val uuid: String,
    @ColumnInfo(name = "parent_uuid")
    val parentUuid: String?,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "created_date")
    val createdDate: Long,
    @ColumnInfo(name = "last_update")
    val lastUpdate: Long,
    @ColumnInfo(name = "share_id")
    val shareId: String,
    @ColumnInfo(name = "at")
    val at: Long,
)

/** Current mirror of a shared task row. */
@Entity(tableName = "host_tasks")
data class HostTaskDTO(
    @PrimaryKey
    @ColumnInfo(name = "uuid")
    val uuid: String,
    @ColumnInfo(name = "parent_uuid")
    val parentUuid: String,
    @ColumnInfo(name = "name")
    val name: String,
    @ColumnInfo(name = "is_completed")
    val isCompleted: Boolean,
    @ColumnInfo(name = "date_created")
    val dateCreated: Long,
    @ColumnInfo(name = "share_id")
    val shareId: String,
    @ColumnInfo(name = "at")
    val at: Long,
)

/**
 * LWW guard for deleted entities: a stale upsert (older `at`) of a tombstoned uuid
 * is ignored instead of resurrecting the row.
 */
@Entity(tableName = "tombstones")
data class ShareTombstoneDTO(
    @PrimaryKey
    @ColumnInfo(name = "uuid")
    val uuid: String,
    /** `checklist` | `task` */
    @ColumnInfo(name = "entity")
    val entity: String,
    @ColumnInfo(name = "share_id")
    val shareId: String,
    @ColumnInfo(name = "at")
    val at: Long,
)

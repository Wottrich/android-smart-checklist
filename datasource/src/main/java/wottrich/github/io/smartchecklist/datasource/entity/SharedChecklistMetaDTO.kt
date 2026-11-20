package wottrich.github.io.smartchecklist.datasource.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Per-checklist share state in the **main** Room database — who hosts this checklist
 * and where pull/push stands. Both the owner (`is_owner = 1`, host is this device,
 * reached via loopback) and guests (`is_owner = 0`) get one row per shared root
 * checklist. `detached = 1` freezes the local copy: no more syncing, no outbox routing.
 */
@Entity(tableName = "shared_checklist_meta")
data class SharedChecklistMetaDTO(
    /** Root checklist uuid. */
    @PrimaryKey
    @ColumnInfo(name = "checklist_uuid")
    val checklistUuid: String,
    /** Stable id of the owner device (the share host). */
    @ColumnInfo(name = "host_device_id")
    val hostDeviceId: String,
    /** Last known host address, `"ip:port"`. */
    @ColumnInfo(name = "host_address")
    val hostAddress: String,
    @ColumnInfo(name = "share_id")
    val shareId: String,
    /** This device's bearer token on that host (guest credential; owner = loopback token). */
    @ColumnInfo(name = "host_token")
    val hostToken: String,
    /** Pull cursor into the host's change log for this share. */
    @ColumnInfo(name = "cursor")
    val cursor: Long = 0,
    /** 1 = this device hosts the share (loopback). */
    @ColumnInfo(name = "is_owner")
    val isOwner: Boolean = false,
    /** 1 = share ended; keep the local frozen copy, never sync again. */
    @ColumnInfo(name = "detached")
    val detached: Boolean = false,
    @ColumnInfo(name = "last_sync_at")
    val lastSyncAt: Long? = null,
)

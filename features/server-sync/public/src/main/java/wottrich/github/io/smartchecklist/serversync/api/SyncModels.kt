package wottrich.github.io.smartchecklist.serversync.api

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Wire DTOs for the self-hosted sync backend — shapes follow
 * `smart-checklist-backend/docs/api/sync-contract.md` (v1) field-for-field.
 * Dates are epoch millis. Never rename a field without checking the contract.
 */

// ---------- payloads (row snapshots inside a change) ----------

/** Mirrors a `ChecklistDTO` row: same field names, `Calendar` fields as epoch millis. */
@Serializable
data class ChecklistSyncDto(
    val uuid: String,
    val name: String,
    val parentUuid: String? = null,
    val isSelected: Boolean = false,
    val createdDate: Long,
    val lastUpdate: Long,
)

/** Mirrors a `TaskDTO` row: same field names, `Calendar` fields as epoch millis. */
@Serializable
data class TaskSyncDto(
    val uuid: String,
    val parentUuid: String,
    val name: String,
    val isCompleted: Boolean = false,
    val dateCreated: Long,
)

// ---------- sync ----------

/**
 * One change pushed by this device. `entity` ∈ `checklist | task`; `op` ∈ `upsert | delete`;
 * `at` is the device time of the mutation (not the send time). The payload is kept as a
 * raw [JsonElement] so upsert and delete shapes (`{"uuid": "…"}`) round-trip untouched.
 */
@Serializable
data class SyncChangeDto(
    val entity: String,
    val uuid: String,
    val op: String,
    val at: Long,
    val payload: JsonElement,
)

/**
 * One change pulled from the server's change log. Same as [SyncChangeDto] plus the
 * [serverSeq] cursor the change was assigned server-side.
 */
@Serializable
data class SyncChangeEntryDto(
    val entity: String,
    val uuid: String,
    val op: String,
    val at: Long,
    val serverSeq: Long,
    val payload: JsonElement,
)

@Serializable
data class SyncPushBody(val changes: List<SyncChangeDto> = emptyList())

@Serializable
data class SyncAckResponse(val uuid: String, val status: String)

@Serializable
data class SyncPushResponse(val results: List<SyncAckResponse> = emptyList())

@Serializable
data class SyncPullResponse(
    val cursor: Long,
    val hasMore: Boolean,
    val changes: List<SyncChangeEntryDto> = emptyList(),
)

// ---------- auth ----------

@Serializable
data class RegisterBody(val name: String? = null)

@Serializable
data class RegisterResponse(val userId: String, val token: String)

@Serializable
data class MeResponse(val userId: String, val name: String? = null)

// ---------- backup ----------

@Serializable
data class BackupStatusResponse(val isConnected: Boolean = true, val lastBackupDate: Long? = null)

// ---------- errors ----------

/** Body of every non-2xx response: `{"code": "<machine>", "message": "<human>", ...}`. */
@Serializable
data class ServerErrorResponse(
    val code: String? = null,
    val message: String? = null,
)

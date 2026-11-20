package wottrich.github.io.smartchecklist.share.api

import kotlinx.coroutines.flow.Flow
import wottrich.github.io.smartchecklist.coroutines.base.Result

/**
 * Host + guest share contract (Wi-Fi checklist sharing, see `docs/plans/wifi-checklist-sharing.md`).
 * The owner flags a checklist → the device hosts it (embedded server + NSD); guests
 * browse nearby offers and accept individually. The owner's copy stays authoritative.
 */
interface ShareRepository {

    // ---------- host side (Phase C/D) ----------

    /**
     * Flags a checklist as shared: creates the share offer, starts hosting when this
     * is the first active share and registers this device as its first guest
     * (loopback — the owner is a client of its own host).
     */
    suspend fun share(rootChecklistUuid: String): Result<ShareHostState>

    /**
     * Stops sharing: the offer disappears; accepted guests keep a frozen local copy
     * (detached) — never silently deleted.
     */
    suspend fun stopSharing(shareId: String): Result<Unit>

    fun observeSharedChecklists(): Flow<List<SharedChecklist>>

    suspend fun getHostStatus(): ShareHostState

    // ---------- pairing (v2 hardening) ----------

    /** The pairing code the owner screen displays right now (`XXXX-XXXX`). */
    fun currentPairingCode(): String

    /** Millis until [currentPairingCode] rotates — for the UI countdown. */
    fun msUntilCodeRotation(): Long

    /**
     * Revokes every paired guest (they keep frozen copies and must re-pair) and
     * rotates the master pairing secret, instantly killing all displayed codes.
     */
    suspend fun resetPairing(): Result<Unit>

    // ---------- guest side (Phase E) ----------

    /**
     * One NSD discovery round on the current Wi-Fi, merged with the manual `ip:port`
     * entries the user added (APs with client isolation block multicast).
     */
    suspend fun scanNearbyHosts(): List<NearbyHost>

    /** Adds a manual `ip:port` host (APs with client isolation block NSD). */
    suspend fun addManualHost(address: String): Result<NearbyHost>

    /**
     * Pairs this device with a host using the owner's displayed pairing code
     * (v2 hardening). Until paired, a host reveals no offers and syncs nothing —
     * the code never travels the network, only an HMAC proof of knowledge.
     */
    suspend fun pairWithCode(host: NearbyHost, code: String): Result<Unit>

    /** Share offers advertised by one host. Requires a paired device token. */
    suspend fun fetchOffers(host: NearbyHost): Result<List<ShareOffer>>

    /**
     * Accepts one offer (requires a paired device): pulls the full share state
     * (cursor 0), inserts the local `shared_checklist_meta` row — the checklist
     * materializes locally and becomes editable.
     */
    suspend fun accept(offer: ShareOffer, host: NearbyHost): Result<Unit>

    /** Local shares that materialized (owned or accepted), for the drawer badge. */
    fun observeAcceptedShares(): Flow<List<GuestShare>>

    /** Detaches locally without waiting for the owner (keeps the frozen copy). */
    suspend fun detach(shareId: String): Result<Unit>
}

data class SharedChecklist(
    val checklistUuid: String,
    val shareId: String,
    val name: String,
    val isOwner: Boolean,
)

data class ShareHostState(
    val isHosting: Boolean,
    val deviceId: String,
    val deviceName: String,
    /** LAN `ip:port` guests can dial, or null when not hosting. */
    val guestAddress: String?,
    /** This device's own loopback handle (`127.0.0.1:port`) while hosting. */
    val port: Int?,
    val guestCount: Int,
)

data class NearbyHost(
    /** Stable host device id (NSD service name / TXT), or `ip:port` for manual entries. */
    val deviceId: String,
    val address: String,
    val isManual: Boolean = false,
)

data class ShareOffer(
    val shareId: String,
    val name: String,
    val ownerDeviceName: String,
    val updatedAt: Long,
)

data class GuestShare(
    val shareId: String,
    val checklistUuid: String,
    val name: String,
    val hostDeviceId: String,
    val isOwner: Boolean,
    val detached: Boolean,
    val lastSyncAt: Long?,
)

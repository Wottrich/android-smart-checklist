package wottrich.github.io.smartchecklist.share.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders
import wottrich.github.io.smartchecklist.datasource.data.datasource.ChecklistDatasource
import wottrich.github.io.smartchecklist.datasource.data.datasource.SharedChecklistMeta
import wottrich.github.io.smartchecklist.datasource.data.datasource.SharedChecklistMetaDatasource
import wottrich.github.io.smartchecklist.datasource.data.datasource.SyncOutboxEntry
import wottrich.github.io.smartchecklist.serversync.api.ChecklistSyncDto
import wottrich.github.io.smartchecklist.serversync.api.HostSyncEngine
import wottrich.github.io.smartchecklist.serversync.api.ServerApi
import wottrich.github.io.smartchecklist.serversync.api.ServerSyncJson
import wottrich.github.io.smartchecklist.serversync.domain.SyncError
import wottrich.github.io.smartchecklist.share.api.GuestShare
import wottrich.github.io.smartchecklist.share.api.NearbyHost
import wottrich.github.io.smartchecklist.share.api.PairRequest
import wottrich.github.io.smartchecklist.share.api.ShareHostState
import wottrich.github.io.smartchecklist.share.api.ShareOffer
import wottrich.github.io.smartchecklist.share.api.ShareRepository
import wottrich.github.io.smartchecklist.share.api.SharedChecklist
import wottrich.github.io.smartchecklist.share.discovery.ShareNsdBrowser
import wottrich.github.io.smartchecklist.share.domain.ShareError
import wottrich.github.io.smartchecklist.share.host.PairingCode
import wottrich.github.io.smartchecklist.share.host.ShareCrypto
import wottrich.github.io.smartchecklist.share.host.ShareHostManager
import wottrich.github.io.smartchecklist.share.host.ShareHostPrefs
import wottrich.github.io.smartchecklist.share.host.ShareHostService
import wottrich.github.io.smartchecklist.uuid.UuidGenerator

/**
 * Host-side implementation (Phase C/D). Guest browsing/accepting over NSD lands in
 * Phase E — those methods are explicit stubs until then.
 */
class ShareRepositoryImpl(
    private val dispatchersProviders: DispatchersProviders,
    private val hostService: ShareHostService,
    private val hostManager: ShareHostManager,
    private val prefs: ShareHostPrefs,
    private val metaDatasource: SharedChecklistMetaDatasource,
    private val checklistDatasource: ChecklistDatasource,
    private val hostClient: ShareHostClient,
    private val serverApi: ServerApi,
    private val hostSyncEngine: HostSyncEngine,
    private val nsdBrowser: ShareNsdBrowser,
) : ShareRepository {

    // ---------- host side ----------

    override suspend fun share(rootChecklistUuid: String): Result<ShareHostState> {
        return withContext(dispatchersProviders.io) {
            val row = checklistDatasource.getChecklistByUuid(rootChecklistUuid)
                ?: return@withContext Result.failure(ShareError.ValidationRejected("checklist not found"))
            try {
                // 1. create the offer
                val shareId = hostService.createShare(
                    rootUuid = rootChecklistUuid,
                    name = row.name,
                )
                // 2. start hosting (first active share) — binds + advertises
                hostManager.ensureStarted()
                val loopback = hostManager.hostAddress()
                    ?: return@withContext Result.failure(ShareError.HostUnreachable(null))
                // 3. the owner becomes its own first guest (loopback symmetry)
                if (prefs.getOwnerToken() == null) {
                    val register = hostService.register(deviceName = prefs.getDeviceName())
                    prefs.setOwnerToken(register.token)
                }
                hostService.acceptSelf(token = prefs.getOwnerToken().orEmpty(), shareId = shareId)
                // the owner knows its own secret trivially — envelopes (Phase S2)
                // derive from it exactly like a guest's, symmetric by construction
                prefs.setGuestPairingSecret(loopback, prefs.getMasterPairingSecret())
                // 4. local routing row — owner edits enqueue against this host
                metaDatasource.upsert(
                    SharedChecklistMeta(
                        checklistUuid = rootChecklistUuid,
                        hostDeviceId = prefs.getDeviceId(),
                        hostAddress = loopback,
                        shareId = shareId,
                        hostToken = prefs.getOwnerToken().orEmpty(),
                        isOwner = true,
                    )
                )
                Result.success(getHostStatus())
            } catch (error: Exception) {
                Result.failure(toShareError(error))
            }
        }
    }

    override suspend fun stopSharing(shareId: String): Result<Unit> {
        return withContext(dispatchersProviders.io) {
            try {
                val share = hostService.endShare(shareId) // offer gone; guests converge to detach
                metaDatasource.deleteByChecklistUuid(share.rootUuid)
                if (hostService.hasActiveShares().not()) {
                    hostManager.ensureStopped()
                }
                Result.success(Unit)
            } catch (error: Exception) {
                Result.failure(toShareError(error))
            }
        }
    }

    override fun observeSharedChecklists(): Flow<List<SharedChecklist>> = flow {
        val shared = mutableListOf<SharedChecklist>()
        for (meta in metaDatasource.getAll().filterNot { it.detached }) {
            shared += SharedChecklist(
                checklistUuid = meta.checklistUuid,
                shareId = meta.shareId,
                name = checklistDatasource.getChecklistByUuid(meta.checklistUuid)?.name.orEmpty(),
                isOwner = meta.isOwner,
            )
        }
        emit(shared)
    }

    override suspend fun getHostStatus(): ShareHostState {
        val hostAddress = hostManager.hostAddress()
        return ShareHostState(
            isHosting = hostManager.isHosting,
            deviceId = prefs.getDeviceId(),
            deviceName = prefs.getDeviceName(),
            guestAddress = hostAddress,
            port = hostAddress?.substringAfter(':')?.toIntOrNull(),
            guestCount = hostService.guestCount(),
        )
    }

    // ---------- pairing (v2 hardening) ----------

    override fun currentPairingCode(): String = hostService.currentPairingCode()

    override fun msUntilCodeRotation(): Long = hostService.msUntilCodeRotation()

    override suspend fun resetPairing(): Result<Unit> {
        return withContext(dispatchersProviders.io) {
            try {
                hostService.resetPairing()
                Result.success(Unit)
            } catch (error: Exception) {
                Result.failure(toShareError(error))
            }
        }
    }

    // ---------- guest side (Phase E) ----------

    override suspend fun scanNearbyHosts(): List<NearbyHost> {
        return withContext(dispatchersProviders.io) {
            val discovered = nsdBrowser.scan()
            val manual = prefs.getManualHosts().map { NearbyHost(deviceId = it, address = it, isManual = true) }
            (manual + discovered).distinctBy { it.address }
        }
    }

    override suspend fun addManualHost(address: String): Result<NearbyHost> {
        val normalized = address.trim().removePrefix("http://").trimEnd('/')
        if (!normalized.contains(':')) {
            return Result.failure(ShareError.ValidationRejected("use the ip:port form"))
        }
        prefs.addManualHost(normalized)
        return Result.success(NearbyHost(deviceId = normalized, address = normalized, isManual = true))
    }

    /**
     * Pairs with a host: proves knowledge of the owner's displayed code (the code
     * itself never travels — only its HMAC), stores the issued device token per
     * host address. Until paired, a host reveals no offers.
     */
    override suspend fun pairWithCode(host: NearbyHost, code: String): Result<Unit> {
        return withContext(dispatchersProviders.io) {
            try {
                val nonce = UuidGenerator.getRandomUuid()
                val deviceName = prefs.getDeviceName()
                val request = PairRequest(
                    deviceName = deviceName,
                    proof = PairingCode.proofOf(code, deviceName, nonce),
                    nonce = nonce,
                )
                val response = hostClient.pair(host.address, request)
                prefs.setGuestToken(host.address, response.token)
                // unwrap the host's pairing secret with the typed code (Phase S2):
                // only a device that knows the code can follow the encrypted wire
                val secret = ShareCrypto.unwrap(response.wrappedPairingSecret, code)
                    ?: throw ShareError.PairingFailed(retryLater = false)
                prefs.setGuestPairingSecret(host.address, secret)
                Result.success(Unit)
            } catch (error: Exception) {
                Result.failure(toShareError(error))
            }
        }
    }

    override suspend fun fetchOffers(host: NearbyHost): Result<List<ShareOffer>> {
        return withContext(dispatchersProviders.io) {
            try {
                val token = prefs.getGuestToken(host.address)
                Result.success(
                    hostClient.fetchOffers(host.address, token).offers.map {
                        ShareOffer(
                            shareId = it.shareId,
                            name = it.name,
                            ownerDeviceName = it.ownerDeviceName,
                            updatedAt = it.updatedAt,
                        )
                    }
                )
            } catch (error: Exception) {
                Result.failure(toShareError(error))
            }
        }
    }

    /**
     * Accepts one offer (requires pairing): grants membership, then materializes
     * the share with a full pull from cursor 0 (engine apply path) — the checklist
     * simply appears in the app, already editable.
     */
    override suspend fun accept(offer: ShareOffer, host: NearbyHost): Result<Unit> {
        return withContext(dispatchersProviders.io) {
            try {
                val token = prefs.getGuestToken(host.address)
                    ?: throw ShareError.PairingRequired()
                hostClient.accept(host.address, token, offer.shareId)
                val rootUuid = materializeShare(
                    address = host.address,
                    token = token,
                    shareId = offer.shareId,
                )
                metaDatasource.upsert(
                    SharedChecklistMeta(
                        checklistUuid = rootUuid,
                        hostDeviceId = host.deviceId,
                        hostAddress = host.address,
                        shareId = offer.shareId,
                        hostToken = token,
                        isOwner = false,
                    )
                )
                Result.success(Unit)
            } catch (error: Exception) {
                Result.failure(toShareError(error))
            }
        }
    }

    override fun observeAcceptedShares(): Flow<List<GuestShare>> = flow {
        val shares = mutableListOf<GuestShare>()
        for (meta in metaDatasource.getAll()) {
            shares += meta.toGuestShare()
        }
        emit(shares)
    }

    override suspend fun detach(shareId: String): Result<Unit> {
        return withContext(dispatchersProviders.io) {
            val meta = metaDatasource.getAll().firstOrNull { it.shareId == shareId }
                ?: return@withContext Result.failure(ShareError.ShareNotFound())
            metaDatasource.setDetached(meta.checklistUuid, detached = true)
            Result.success(Unit)
        }
    }

    /** Full pull from cursor 0; returns the share's root checklist uuid. */
    private suspend fun materializeShare(address: String, token: String, shareId: String): String {
        val baseUrl = "http://$address"
        var since = 0L
        var rootUuid: String? = null
        while (true) {
            val page = serverApi.pull(baseUrl, token, since = since, limit = PULL_PAGE_SIZE, shareId = shareId)
            hostSyncEngine.applyChanges(page.changes)
            for (change in page.changes) {
                if (rootUuid == null &&
                    change.entity == SyncOutboxEntry.ENTITY_CHECKLIST &&
                    change.op == SyncOutboxEntry.OP_UPSERT
                ) {
                    val payload = runCatching {
                        ServerSyncJson.decodeFromString<ChecklistSyncDto>(change.payload.toString())
                    }.getOrNull()
                    if (payload?.parentUuid == null) rootUuid = payload?.uuid
                }
            }
            since = page.cursor
            if (!page.hasMore) break
        }
        return rootUuid ?: throw ShareError.ValidationRejected("share answered without a root checklist")
    }

    private suspend fun SharedChecklistMeta.toGuestShare() = GuestShare(
        shareId = shareId,
        checklistUuid = checklistUuid,
        name = checklistDatasource.getChecklistByUuid(checklistUuid)?.name.orEmpty(),
        hostDeviceId = hostDeviceId,
        isOwner = isOwner,
        detached = detached,
        lastSyncAt = lastSyncAt,
    )

    private companion object {
        const val PULL_PAGE_SIZE = 500
    }
}

/**
 * Normalizes everything the share flow can throw into [ShareError] — the shared
 * `ServerApi`/engine throw `SyncError`, and a raw one escaping into a ViewModel
 * coroutine is a crash, not a failure state.
 */
private fun toShareError(error: Exception): ShareError = when (error) {
    is ShareError -> error
    is SyncError -> when (error) {
        is SyncError.ServerUnauthorized -> ShareError.Unauthenticated()
        is SyncError.ShareNotFound -> ShareError.ShareNotFound()
        is SyncError.ShareRevoked -> ShareError.DeviceRevoked()
        is SyncError.ValidationRejected -> ShareError.ValidationRejected(error.message ?: "validation failed")
        else -> ShareError.HostUnreachable(error)
    }
    else -> ShareError.HostUnreachable(error)
}

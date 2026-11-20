package wottrich.github.io.smartchecklist.share.sync

import kotlinx.coroutines.withContext
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders
import wottrich.github.io.smartchecklist.datasource.data.datasource.SharedChecklistMeta
import wottrich.github.io.smartchecklist.datasource.data.datasource.SharedChecklistMetaDatasource
import wottrich.github.io.smartchecklist.serversync.api.HostSyncEngine
import wottrich.github.io.smartchecklist.serversync.api.SyncCursorStore
import wottrich.github.io.smartchecklist.serversync.api.SyncParticipant
import wottrich.github.io.smartchecklist.serversync.domain.SyncError
import wottrich.github.io.smartchecklist.share.host.ShareHostManager
import wottrich.github.io.smartchecklist.share.host.ShareHostPrefs

/**
 * Per-host sync sessions (Wi-Fi sharing). Each active host — the PC backend is
 * `ServerSyncRepositoryImpl`'s business, not this driver's — gets its own push/pull
 * loop over the shared [HostSyncEngine]:
 *
 *  - **owner (loopback)**: the device pushes/pulls against its own embedded host at
 *    `127.0.0.1:<port>` with its own guest token — the owner is a client of its own
 *    server, symmetric with every guest;
 *  - **guest**: pushes/pulls the owner device's last-known LAN address with the
 *    per-host credential stored in `shared_checklist_meta`.
 *
 * Unreachable host ⇒ entries stay queued (offline-first). Each share keeps its own
 * pull cursor, so one host with several shares pulls each subtree independently.
 */
class ShareSyncDriver(
    private val dispatchersProviders: DispatchersProviders,
    private val metaDatasource: SharedChecklistMetaDatasource,
    private val hostSyncEngine: HostSyncEngine,
    private val hostManager: ShareHostManager,
    private val prefs: ShareHostPrefs,
) : SyncParticipant {

    override suspend fun onSyncRequested() {
        syncShareHosts()
    }

    suspend fun syncShareHosts() {
        return withContext(dispatchersProviders.io) {
            val metas = metaDatasource.getAll().filterNot { it.detached }
            for ((hostId, hostMetas) in metas.groupBy { it.hostDeviceId }) {
                val session = resolveSession(hostId, hostMetas) ?: continue
                val pushOutcome = runCatching {
                    hostSyncEngine.pushOutbox(hostId = hostId, baseUrl = session.baseUrl, token = session.token)
                }
                // share ended / owner reinstalled / device revoked (v2) → detach
                if (isDetached(pushOutcome.exceptionOrNull())) {
                    detachAll(hostMetas)
                    continue
                }
                for (meta in hostMetas.filter { !it.detached }) {
                    val pullOutcome = runCatching {
                        hostSyncEngine.pullChanges(
                            baseUrl = session.baseUrl,
                            token = session.token,
                            shareId = meta.shareId,
                            cursorStore = MetaCursorStore(metaDatasource, meta.checklistUuid),
                        )
                        metaDatasource.setLastSyncAt(meta.checklistUuid, System.currentTimeMillis())
                    }
                    if (isDetached(pullOutcome.exceptionOrNull())) {
                        metaDatasource.setDetached(meta.checklistUuid, detached = true)
                    }
                    // other failures (offline owner, app closed, port changed):
                    // entries stay queued — convergence happens on a later session
                }
            }
        }
    }

    /** Host-side verdicts that mean "this device will never sync again": detach. */
    private fun isDetached(error: Throwable?): Boolean =
        error is SyncError.ShareNotFound || error is SyncError.ShareRevoked

    private suspend fun detachAll(hostMetas: List<SharedChecklistMeta>) {
        for (meta in hostMetas) {
            metaDatasource.setDetached(meta.checklistUuid, detached = true)
        }
    }

    private fun resolveSession(hostId: String, hostMetas: List<SharedChecklistMeta>): HostSession? {
        return if (hostId == prefs.getDeviceId()) {
            val boundAddress = hostManager.hostAddress() ?: return null
            val token = prefs.getOwnerToken() ?: return null
            HostSession(baseUrl = "http://$boundAddress", token = token)
        } else {
            HostSession(baseUrl = "http://${hostMetas.first().hostAddress}", token = hostMetas.first().hostToken)
        }
    }

    private data class HostSession(val baseUrl: String, val token: String)
}

/** Per-share pull cursor, persisted in `shared_checklist_meta`. */
private class MetaCursorStore(
    private val metaDatasource: SharedChecklistMetaDatasource,
    private val checklistUuid: String,
) : SyncCursorStore {
    override suspend fun get(): Long =
        metaDatasource.getByChecklistUuid(checklistUuid)?.cursor ?: 0L

    override suspend fun set(cursor: Long) {
        metaDatasource.setCursor(checklistUuid, cursor)
    }
}

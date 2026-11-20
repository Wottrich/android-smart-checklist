package wottrich.github.io.smartchecklist.serversync.api

import wottrich.github.io.smartchecklist.serversync.domain.SyncError

/**
 * Raw HTTP surface of a sync target (`share-contract.md` + sync-contract v1).
 * Serves both the PC backend and device share hosts (guests + loopback owner).
 * Every method maps failures to [SyncError]; transport problems (refused,
 * timeout, DNS) become [SyncError.ServerUnreachable].
 */
interface ServerApi {

    suspend fun register(baseUrl: String, deviceName: String?): RegisterResponse

    suspend fun me(baseUrl: String, token: String): MeResponse

    suspend fun push(baseUrl: String, token: String, body: SyncPushBody): SyncPushResponse

    /**
     * @param shareId scopes the pull to one share subtree (device-hosted shares,
     * `share-contract.md`); `null` = the whole change log (PC backend, v1). The
     * param is only appended when non-null, so v1 backends see the exact v1 URL.
     */
    suspend fun pull(
        baseUrl: String,
        token: String,
        since: Long,
        limit: Int,
        shareId: String? = null,
    ): SyncPullResponse

    /** @throws SyncError.NoBackupFound when the server has no backup stored. */
    suspend fun downloadBackup(baseUrl: String, token: String): String

    suspend fun uploadBackup(baseUrl: String, token: String, backupJson: String)

    suspend fun backupStatus(baseUrl: String, token: String): BackupStatusResponse
}

package wottrich.github.io.smartchecklist.serversync.data

import android.content.Context

/**
 * Key-value storage of the server-sync pairing + engine state. Same plain
 * SharedPreferences pattern as `BackupPreferencesDatasource` — the token is a
 * LAN-only secret; revisit if the backend ever leaves the LAN.
 *
 * Single writer rule: `syncCursor` / `lastSyncAt` / `lastErrorKind` are written
 * only by the sync engine, never by the settings UI.
 */
interface ServerSettingsDatasource {

    fun isConfigured(): Boolean

    fun getServerUrl(): String?

    fun getUserId(): String?

    fun getToken(): String?

    /** Batched pairing write — one commit for url/token/userId. */
    fun setConnection(serverUrl: String, token: String, userId: String)

    /** Clears pairing; never deletes local data. */
    fun clearConnection()

    fun getSyncCursor(): Long

    fun setSyncCursor(cursor: Long)

    fun getLastSyncAt(): Long?

    fun setLastSyncAt(at: Long?)

    fun getLastErrorKind(): String?

    fun setLastErrorKind(kind: String?)
}

internal class ServerSettingsDatasourceImpl(
    context: Context
) : ServerSettingsDatasource {

    private val preferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun isConfigured(): Boolean =
        !preferences.getString(KEY_SERVER_URL, null).isNullOrEmpty() &&
            !preferences.getString(KEY_TOKEN, null).isNullOrEmpty()

    override fun getServerUrl(): String? =
        preferences.getString(KEY_SERVER_URL, null)

    override fun getUserId(): String? =
        preferences.getString(KEY_USER_ID, null)

    override fun getToken(): String? =
        preferences.getString(KEY_TOKEN, null)

    override fun setConnection(serverUrl: String, token: String, userId: String) {
        preferences.edit().apply {
            putString(KEY_SERVER_URL, serverUrl.trimEnd('/'))
            putString(KEY_TOKEN, token)
            putString(KEY_USER_ID, userId)
        }.apply()
    }

    override fun clearConnection() {
        preferences.edit().clear().apply()
    }

    override fun getSyncCursor(): Long =
        preferences.getLong(KEY_SYNC_CURSOR, 0L)

    override fun setSyncCursor(cursor: Long) {
        preferences.edit().putLong(KEY_SYNC_CURSOR, cursor).apply()
    }

    override fun getLastSyncAt(): Long? {
        if (!preferences.contains(KEY_LAST_SYNC_AT)) return null
        return preferences.getLong(KEY_LAST_SYNC_AT, 0L)
    }

    override fun setLastSyncAt(at: Long?) {
        preferences.edit().apply {
            if (at == null) {
                remove(KEY_LAST_SYNC_AT)
            } else {
                putLong(KEY_LAST_SYNC_AT, at)
            }
        }.apply()
    }

    override fun getLastErrorKind(): String? =
        preferences.getString(KEY_LAST_ERROR_KIND, null)

    override fun setLastErrorKind(kind: String?) {
        preferences.edit().apply {
            if (kind == null) {
                remove(KEY_LAST_ERROR_KIND)
            } else {
                putString(KEY_LAST_ERROR_KIND, kind)
            }
        }.apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "server_sync_prefs"
        const val KEY_SERVER_URL = "serverUrl"
        const val KEY_USER_ID = "userId"
        const val KEY_TOKEN = "token"
        const val KEY_SYNC_CURSOR = "syncCursor"
        const val KEY_LAST_SYNC_AT = "lastSyncAt"
        const val KEY_LAST_ERROR_KIND = "lastErrorKind"
    }
}

package wottrich.github.io.smartchecklist.backup.data

import android.content.Context

/**
 * Key-value storage of the backup feature — the first of its kind in the app.
 * Deliberately stores **no tokens**, only non-secret display state:
 * connection flag, connected account email and last backup date.
 */
interface BackupPreferencesDatasource {

    fun isConnected(): Boolean

    fun getConnectedAccountEmail(): String?

    /** Batched write — one commit for all connection-state changes. */
    fun setConnectionState(connected: Boolean, connectedAccountEmail: String?, lastBackupDate: Long?)

    fun getLastBackupDate(): Long?

    fun setLastBackupDate(date: Long?)
}

internal class BackupPreferencesDatasourceImpl(
    context: Context
) : BackupPreferencesDatasource {

    private val preferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun isConnected(): Boolean =
        preferences.getBoolean(KEY_CONNECTED, false)

    override fun getConnectedAccountEmail(): String? =
        preferences.getString(KEY_CONNECTED_ACCOUNT_EMAIL, null)

    override fun setConnectionState(connected: Boolean, connectedAccountEmail: String?, lastBackupDate: Long?) {
        preferences.edit().apply {
            putBoolean(KEY_CONNECTED, connected)
            if (connectedAccountEmail == null) {
                remove(KEY_CONNECTED_ACCOUNT_EMAIL)
            } else {
                putString(KEY_CONNECTED_ACCOUNT_EMAIL, connectedAccountEmail)
            }
            if (lastBackupDate == null) {
                remove(KEY_LAST_BACKUP_DATE)
            } else {
                putLong(KEY_LAST_BACKUP_DATE, lastBackupDate)
            }
        }.apply()
    }

    override fun getLastBackupDate(): Long? {
        if (!preferences.contains(KEY_LAST_BACKUP_DATE)) return null
        return preferences.getLong(KEY_LAST_BACKUP_DATE, 0L)
    }

    override fun setLastBackupDate(date: Long?) {
        preferences.edit().apply {
            if (date == null) {
                remove(KEY_LAST_BACKUP_DATE)
            } else {
                putLong(KEY_LAST_BACKUP_DATE, date)
            }
        }.apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "backup_prefs"
        const val KEY_CONNECTED = "connected"
        const val KEY_CONNECTED_ACCOUNT_EMAIL = "connectedAccountEmail"
        const val KEY_LAST_BACKUP_DATE = "lastBackupDate"
    }
}

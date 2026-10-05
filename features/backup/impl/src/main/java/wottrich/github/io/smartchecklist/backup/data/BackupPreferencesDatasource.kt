package wottrich.github.io.smartchecklist.backup.data

import android.content.Context

/**
 * Key-value storage of the backup feature — the first of its kind in the app.
 * Deliberately stores **no tokens**, only non-secret display state:
 * the connected account email and the last backup date.
 */
interface BackupPreferencesDatasource {

    fun getConnectedAccountEmail(): String?

    fun setConnectedAccountEmail(email: String?)

    fun getLastBackupDate(): Long?

    fun setLastBackupDate(date: Long?)
}

internal class BackupPreferencesDatasourceImpl(
    context: Context
) : BackupPreferencesDatasource {

    private val preferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun getConnectedAccountEmail(): String? =
        preferences.getString(KEY_CONNECTED_ACCOUNT_EMAIL, null)

    override fun setConnectedAccountEmail(email: String?) {
        preferences.edit().apply {
            if (email == null) {
                remove(KEY_CONNECTED_ACCOUNT_EMAIL)
            } else {
                putString(KEY_CONNECTED_ACCOUNT_EMAIL, email)
            }
        }.apply()
    }

    override fun getLastBackupDate(): Long? {
        val value = preferences.getLong(KEY_LAST_BACKUP_DATE, NEVER_BACKED_UP)
        return if (value == NEVER_BACKED_UP) null else value
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
        const val KEY_CONNECTED_ACCOUNT_EMAIL = "connectedAccountEmail"
        const val KEY_LAST_BACKUP_DATE = "lastBackupDate"
        const val NEVER_BACKED_UP = -1L
    }
}

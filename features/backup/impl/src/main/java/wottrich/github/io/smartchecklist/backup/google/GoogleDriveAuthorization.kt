package wottrich.github.io.smartchecklist.backup.google

import android.app.PendingIntent

/**
 * Authorization seam of the backup feature — the only class hierarchy allowed to
 * talk to Credential Manager (account choice) and `AuthorizationClient`
 * (`drive.appdata` scope grant).
 *
 * Kept internal to `:features:backup:impl` on purpose: the public backup module is
 * pure Kotlin, so consent resolution travels through the ViewModel effects
 * (`BackupUiEffects.RequestConsent`) instead of the public repository contract.
 *
 * Tokens are never persisted — they expire (~1h); call [authorize] before each
 * Drive operation (silent when the user already granted the scope).
 */
interface GoogleDriveAuthorization {

    /**
     * Silent-first authorization. Returns [AuthorizationOutcome.Granted] when a
     * token is available, [AuthorizationOutcome.NeedsConsent] when the user must
     * interactively grant the scope (launch the resolvable pending intent, then
     * call this again after the consent result), or [AuthorizationOutcome.Failed].
     */
    suspend fun authorize(): AuthorizationOutcome

    /** Revokes the `drive.appdata` grant (used on disconnect). */
    suspend fun revokeAccess()

    sealed class AuthorizationOutcome {
        data class Granted(val accessToken: String, val accountEmail: String?) : AuthorizationOutcome()
        data class NeedsConsent(val resolvablePendingIntent: PendingIntent) : AuthorizationOutcome()
        data class Failed(val exception: Exception) : AuthorizationOutcome()
    }
}

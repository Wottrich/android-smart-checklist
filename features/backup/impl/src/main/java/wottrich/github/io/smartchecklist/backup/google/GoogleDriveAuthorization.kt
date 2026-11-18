package wottrich.github.io.smartchecklist.backup.google

import android.app.PendingIntent
import android.content.Intent

/**
 * Authorization seam of the backup feature — the only class allowed to talk to
 * `AuthorizationClient` (`play-services-auth`) for the `drive.appdata` scope grant.
 *
 * Kept internal to `:features:backup:impl` on purpose: the public backup module is
 * pure Kotlin, so consent resolution travels through the ViewModel effects
 * (`BackupUiEffects.RequestConsent`) instead of the public repository contract.
 *
 * Flow: [authorize] silent-first. On [AuthorizationOutcome.NeedsConsent] the UI
 * launches the pending intent; when it returns OK, the result `data` intent MUST
 * be consumed through [completeConsent] (`getAuthorizationResultFromIntent`) —
 * skipping that step makes Play services keep answering `hasResolution() == true`
 * forever, even after the user granted the scope.
 *
 * Tokens are never persisted — they expire (~1h); call [authorize] before each
 * Drive operation (silent when the user already granted the scope).
 */
interface GoogleDriveAuthorization {

    /**
     * Silent-first authorization. Returns [AuthorizationOutcome.Granted] when a
     * token is available, [AuthorizationOutcome.NeedsConsent] when the user must
     * interactively grant the scope (launch the pending intent, then call
     * [completeConsent] with the activity result), or [AuthorizationOutcome.Failed].
     */
    suspend fun authorize(): AuthorizationOutcome

    /**
     * Consumes the consent activity result (`onActivityResult` data intent) and
     * extracts the authorization outcome — the access token comes from this
     * result, not from calling [authorize] again.
     */
    suspend fun completeConsent(consentResultIntent: Intent?): AuthorizationOutcome

    /** Revokes the `drive.appdata` grant (used on disconnect). */
    suspend fun revokeAccess()

    sealed class AuthorizationOutcome {
        data class Granted(val accessToken: String, val accountEmail: String?) : AuthorizationOutcome()
        data class NeedsConsent(val resolvablePendingIntent: PendingIntent) : AuthorizationOutcome()
        data class Failed(val exception: Exception) : AuthorizationOutcome()
    }
}

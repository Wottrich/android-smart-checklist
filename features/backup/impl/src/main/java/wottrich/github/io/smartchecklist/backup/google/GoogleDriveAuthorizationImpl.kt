package wottrich.github.io.smartchecklist.backup.google

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.RevokeAccessRequest
import com.google.android.gms.common.api.Scope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import wottrich.github.io.smartchecklist.backup.google.GoogleDriveAuthorization.AuthorizationOutcome
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders

/**
 * Modern Google auth split into its two halves:
 *
 * 1. **Authentication** (which account) — Credential Manager
 *    (`androidx.credentials` + `googleid`). Tries previously authorized accounts
 *    first and falls back to the account picker.
 * 2. **Authorization** (`drive.appdata` scope) — `AuthorizationClient`
 *    (`play-services-auth`). Silent when already granted; otherwise returns the
 *    resolvable consent intent through [AuthorizationOutcome.NeedsConsent].
 *
 * No token is ever persisted — access tokens expire (~1h), so [authorize] is
 * called before each Drive operation.
 */
internal class GoogleDriveAuthorizationImpl(
    private val context: Context,
    private val dispatchersProviders: DispatchersProviders,
) : GoogleDriveAuthorization {

    private val credentialManager by lazy { CredentialManager.create(context) }
    private val authorizationClient by lazy { Identity.getAuthorizationClient(context) }

    override suspend fun authorize(): AuthorizationOutcome {
        if (GoogleDriveBackupConfig.WEB_CLIENT_ID.isBlank()) {
            return AuthorizationOutcome.Failed(
                IllegalStateException(
                    "Google Drive backup is not configured: set the Web client ID in " +
                        "GoogleDriveBackupConfig (see docs/plans/google-drive-backup.md Step 0)."
                )
            )
        }
        val accountEmail = withContext(dispatchersProviders.io) { requestAccountEmail() }
        return withContext(dispatchersProviders.io) { authorizeDriveScope(accountEmail) }
    }

    /**
     * Requests the `drive.appdata` scope. Silent-first: when the consent was
     * already granted (even in a previous app install) the token comes back
     * without user interaction.
     */
    private suspend fun authorizeDriveScope(accountEmail: String?): AuthorizationOutcome {
        val request = AuthorizationRequest.Builder()
            .setRequestedScopes(listOf(Scope(DriveScopes.DRIVE_APPDATA)))
            .build()
        val result = try {
            authorizationClient.authorize(request).await()
        } catch (exception: Exception) {
            return AuthorizationOutcome.Failed(exception)
        }
        return result.toOutcome(accountEmail)
    }

    private fun AuthorizationResult.toOutcome(accountEmail: String?): AuthorizationOutcome {
        return if (hasResolution()) {
            val pendingIntent = pendingIntent
                ?: return AuthorizationOutcome.Failed(IllegalStateException("Consent resolution without pending intent"))
            AuthorizationOutcome.NeedsConsent(pendingIntent)
        } else {
            val accessToken = accessToken
            if (accessToken.isNullOrBlank()) {
                AuthorizationOutcome.Failed(IllegalStateException("Authorization result without access token"))
            } else {
                AuthorizationOutcome.Granted(accessToken, accountEmail)
            }
        }
    }

    /** Credential Manager account selection; `null` when the user dismissed the picker. */
    private suspend fun requestAccountEmail(): String? {
        return try {
            getCredentialEmail(filterByAuthorizedAccounts = true)
        } catch (expected: NoCredentialException) {
            // No already-authorized account: show the full account picker.
            try {
                getCredentialEmail(filterByAuthorizedAccounts = false)
            } catch (exception: GetCredentialException) {
                null
            }
        } catch (exception: GetCredentialException) {
            null
        }
    }

    private suspend fun getCredentialEmail(filterByAuthorizedAccounts: Boolean): String? {
        val googleIdOption = GetGoogleIdOption.Builder()
            .setServerClientId(GoogleDriveBackupConfig.WEB_CLIENT_ID)
            .setFilterByAuthorizedAccounts(filterByAuthorizedAccounts)
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
        val response = credentialManager.getCredential(context, request)
        val credential = response.credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            return GoogleIdTokenCredential.createFrom(credential.data).id
        }
        return null
    }

    override suspend fun revokeAccess() {
        withContext(dispatchersProviders.io) {
            try {
                val request = RevokeAccessRequest.builder()
                    .setScopes(listOf(Scope(DriveScopes.DRIVE_APPDATA)))
                    .build()
                authorizationClient.revokeAccess(request).await()
            } catch (ignored: Exception) {
                // Revocation is best-effort; disconnect must not fail because of it.
            }
            try {
                credentialManager.clearCredentialState(ClearCredentialStateRequest())
            } catch (ignored: Exception) {
                // Same as above — best-effort cleanup only.
            }
        }
    }
}

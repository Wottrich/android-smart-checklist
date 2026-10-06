package wottrich.github.io.smartchecklist.backup.google

import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.RevokeAccessRequest
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import wottrich.github.io.smartchecklist.backup.google.GoogleDriveAuthorization.AuthorizationOutcome
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders

/**
 * Authorization via `AuthorizationClient` (`play-services-auth`) with the
 * `drive.appdata` scope.
 *
 * Silent-first: when the scope was already granted (even in a previous app
 * install) the token comes back without user interaction. Otherwise
 * [AuthorizationResult.hasResolution] carries the resolvable consent intent —
 * Google Play services shows both the account picker and the consent screen in
 * that flow, so no separate account-selection step is needed.
 *
 * No token is ever persisted — access tokens expire (~1h), so [authorize] is
 * called before each Drive operation.
 *
 * Requires the Google Cloud setup from docs/plans/google-drive-backup.md Step 0
 * (Drive API enabled + Android OAuth client with this app's SHA-1), otherwise
 * Play services answers with `ApiException` and the outcome is
 * [AuthorizationOutcome.Failed].
 */
internal class GoogleDriveAuthorizationImpl(
    private val context: Context,
    private val dispatchersProviders: DispatchersProviders,
) : GoogleDriveAuthorization {

    private val authorizationClient by lazy { Identity.getAuthorizationClient(context) }

    override suspend fun authorize(): AuthorizationOutcome {
        return withContext(dispatchersProviders.io) { authorizeDriveScope() }
    }

    /**
     * Requests the `drive.appdata` scope. Account selection happens inside the
     * resolvable consent intent when needed.
     */
    private suspend fun authorizeDriveScope(): AuthorizationOutcome {
        val request = AuthorizationRequest.Builder()
            .setRequestedScopes(listOf(Scope(DriveScopes.DRIVE_APPDATA)))
            .build()
        val result = try {
            authorizationClient.authorize(request).await()
        } catch (exception: Exception) {
            return AuthorizationOutcome.Failed(exception)
        }
        return result.toOutcome()
    }

    override suspend fun completeConsent(consentResultIntent: Intent?): AuthorizationOutcome {
        if (consentResultIntent == null) {
            return AuthorizationOutcome.Failed(IllegalStateException("Consent result finished without data"))
        }
        return withContext(dispatchersProviders.io) {
            try {
                authorizationClient.getAuthorizationResultFromIntent(consentResultIntent).toOutcome()
            } catch (exception: Exception) {
                AuthorizationOutcome.Failed(exception)
            }
        }
    }

    private fun AuthorizationResult.toOutcome(): AuthorizationOutcome {
        return if (hasResolution()) {
            val pendingIntent = pendingIntent
                ?: return AuthorizationOutcome.Failed(IllegalStateException("Consent resolution without pending intent"))
            AuthorizationOutcome.NeedsConsent(pendingIntent)
        } else {
            val accessToken = accessToken
            if (accessToken.isNullOrBlank()) {
                AuthorizationOutcome.Failed(IllegalStateException("Authorization result without access token"))
            } else {
                AuthorizationOutcome.Granted(accessToken, accountEmail = toGoogleSignInAccount()?.email)
            }
        }
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
        }
    }
}

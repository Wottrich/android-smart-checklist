package wottrich.github.io.smartchecklist.backup.google

import android.content.Intent

/**
 * Test fake for [GoogleDriveAuthorization] — MockK cannot stub methods whose
 * return type is the abstract sealed [GoogleDriveAuthorization.AuthorizationOutcome].
 */
class FakeGoogleDriveAuthorization(
    var outcome: GoogleDriveAuthorization.AuthorizationOutcome
) : GoogleDriveAuthorization {

    var consentOutcome: GoogleDriveAuthorization.AuthorizationOutcome = outcome

    var completeConsentIntents: MutableList<Intent?> = mutableListOf()
        private set

    var revokeAccessCalls: Int = 0
        private set

    override suspend fun authorize(): GoogleDriveAuthorization.AuthorizationOutcome = outcome

    override suspend fun completeConsent(consentResultIntent: Intent?): GoogleDriveAuthorization.AuthorizationOutcome {
        completeConsentIntents.add(consentResultIntent)
        return consentOutcome
    }

    override suspend fun revokeAccess() {
        revokeAccessCalls++
    }
}

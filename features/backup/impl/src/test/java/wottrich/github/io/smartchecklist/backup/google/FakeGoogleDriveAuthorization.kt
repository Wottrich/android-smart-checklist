package wottrich.github.io.smartchecklist.backup.google

/**
 * Test fake for [GoogleDriveAuthorization] — MockK cannot stub methods whose
 * return type is the abstract sealed [GoogleDriveAuthorization.AuthorizationOutcome].
 */
class FakeGoogleDriveAuthorization(
    var outcome: GoogleDriveAuthorization.AuthorizationOutcome
) : GoogleDriveAuthorization {

    var revokeAccessCalls: Int = 0
        private set

    override suspend fun authorize(): GoogleDriveAuthorization.AuthorizationOutcome = outcome

    override suspend fun revokeAccess() {
        revokeAccessCalls++
    }
}

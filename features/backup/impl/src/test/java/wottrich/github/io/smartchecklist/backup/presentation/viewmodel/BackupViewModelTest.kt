package wottrich.github.io.smartchecklist.backup.presentation.viewmodel

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import wottrich.github.io.smartchecklist.backup.R
import wottrich.github.io.smartchecklist.backup.data.repository.BackupStatusModel
import wottrich.github.io.smartchecklist.backup.domain.BackupError
import wottrich.github.io.smartchecklist.backup.domain.ConnectGoogleDriveUseCase
import wottrich.github.io.smartchecklist.backup.domain.CreateBackupUseCase
import wottrich.github.io.smartchecklist.backup.domain.DisconnectGoogleDriveUseCase
import wottrich.github.io.smartchecklist.backup.domain.GetBackupStatusUseCase
import wottrich.github.io.smartchecklist.backup.domain.RestoreBackupUseCase
import wottrich.github.io.smartchecklist.backup.google.FakeGoogleDriveAuthorization
import wottrich.github.io.smartchecklist.backup.google.GoogleDriveAuthorization
import wottrich.github.io.smartchecklist.backup.presentation.state.BackupUiActions
import wottrich.github.io.smartchecklist.backup.presentation.state.BackupUiEffects
import wottrich.github.io.smartchecklist.backup.presentation.state.BackupUiState
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.coroutines.failureEmptyResult
import wottrich.github.io.smartchecklist.coroutines.successEmptyResult
import wottrich.github.io.smartchecklist.testtools.BaseUnitTest

@OptIn(ExperimentalCoroutinesApi::class)
class BackupViewModelTest : BaseUnitTest() {

    private lateinit var sut: BackupViewModel

    private val getBackupStatusUseCase: GetBackupStatusUseCase = mockk()
    private val connectGoogleDriveUseCase: ConnectGoogleDriveUseCase = mockk()
    private val disconnectGoogleDriveUseCase: DisconnectGoogleDriveUseCase = mockk()
    private val createBackupUseCase: CreateBackupUseCase = mockk()
    private val restoreBackupUseCase: RestoreBackupUseCase = mockk()
    private val grantedOutcome =
        GoogleDriveAuthorization.AuthorizationOutcome.Granted(accessToken = "token", accountEmail = "user@gmail.com")
    private val googleDriveAuthorization = FakeGoogleDriveAuthorization(grantedOutcome)

    private fun buildSut() {
        googleDriveAuthorization.outcome = grantedOutcome
        sut = BackupViewModel(
            dispatchersProviders = coroutinesTestRule.dispatchers,
            getBackupStatusUseCase = getBackupStatusUseCase,
            connectGoogleDriveUseCase = connectGoogleDriveUseCase,
            disconnectGoogleDriveUseCase = disconnectGoogleDriveUseCase,
            createBackupUseCase = createBackupUseCase,
            restoreBackupUseCase = restoreBackupUseCase,
            googleDriveAuthorization = googleDriveAuthorization
        )
    }

    private fun stubStatus(email: String?, lastBackupDate: Long? = null) {
        coEvery { getBackupStatusUseCase() } returns Result.success(
            BackupStatusModel(connectedAccountEmail = email, lastBackupDate = lastBackupDate)
        )
    }

    @Test
    fun `GIVEN a persisted status WHEN viewmodel is created THEN initial state must show the overview with the connected account`() =
        runBlockingUnitTest {
            stubStatus(email = "user@gmail.com", lastBackupDate = 1000L)

            buildSut()

            val state = sut.uiState.first()
            assertTrue(state is BackupUiState.Overview)
            assertEquals("user@gmail.com", (state as BackupUiState.Overview).connectedAccountEmail)
            assertEquals(1000L, state.lastBackupDate)
        }

    @Test
    fun `GIVEN connect succeeds WHEN ConnectAction is sent THEN state must show the connected email`() =
        runBlockingUnitTest {
            stubStatus(email = "user@gmail.com")
            coEvery { connectGoogleDriveUseCase() } returns Result.success(
                BackupStatusModel(connectedAccountEmail = "user@gmail.com", lastBackupDate = null)
            )
            buildSut()

            sut.sendAction(BackupUiActions.Action.ConnectAction)

            val state = sut.uiState.first()
            assertTrue(state is BackupUiState.Overview && state.connectedAccountEmail == "user@gmail.com")
        }

    @Test
    fun `GIVEN consent is required WHEN ConnectAction is sent THEN RequestConsent effect must be emitted with the resolvable pending intent`() =
        runBlockingUnitTest {
            val expectedPendingIntent = mockk<android.app.PendingIntent>()
            stubStatus(email = null)
            coEvery { connectGoogleDriveUseCase() } returns failureWith(BackupError.NeedsConsent())
            buildSut()
            googleDriveAuthorization.outcome =
                GoogleDriveAuthorization.AuthorizationOutcome.NeedsConsent(expectedPendingIntent)

            sut.sendAction(BackupUiActions.Action.ConnectAction)

            val effect = sut.uiEffects.first()
            assertTrue(effect is BackupUiEffects.RequestConsent)
            assertEquals(
                expectedPendingIntent,
                (effect as BackupUiEffects.RequestConsent).resolvablePendingIntent
            )
        }

    @Test
    fun `GIVEN connect fails WHEN ConnectAction is sent THEN a snackbar error effect must be emitted`() =
        runBlockingUnitTest {
            stubStatus(email = null)
            coEvery { connectGoogleDriveUseCase() } returns failureWith(BackupError.DriveIoError(java.io.IOException()))
            buildSut()

            sut.sendAction(BackupUiActions.Action.ConnectAction)

            val effect = sut.uiEffects.first()
            assertEquals(
                BackupUiEffects.SnackbarError(R.string.backup_error_drive_io),
                effect
            )
        }

    @Test
    fun `GIVEN restore succeeds WHEN RestoreAction is sent THEN RestoreCompleted effect must be emitted`() =
        runBlockingUnitTest {
            stubStatus(email = "user@gmail.com", lastBackupDate = 1000L)
            coEvery { restoreBackupUseCase() } returns successEmptyResult()
            buildSut()

            sut.sendAction(BackupUiActions.Action.RestoreAction)

            val effect = sut.uiEffects.first()
            assertEquals(BackupUiEffects.RestoreCompleted, effect)
        }

    @Test
    fun `GIVEN backup succeeds WHEN BackupNowAction is sent THEN BackupCompleted effect must be emitted and last backup date refreshed`() =
        runBlockingUnitTest {
            stubStatus(email = "user@gmail.com", lastBackupDate = 1000L)
            coEvery { createBackupUseCase() } returns successEmptyResult()
            buildSut()

            sut.sendAction(BackupUiActions.Action.BackupNowAction)

            val effect = sut.uiEffects.first()
            assertEquals(BackupUiEffects.BackupCompleted, effect)
            val state = sut.uiState.first()
            assertTrue(state is BackupUiState.Overview && state.lastBackupDate == 1000L)
        }

    @Test
    fun `GIVEN disconnect succeeds WHEN DisconnectAction is sent THEN DisconnectCompleted effect must be emitted and state must be cleared`() =
        runBlockingUnitTest {
            stubStatus(email = null, lastBackupDate = null)
            coEvery { disconnectGoogleDriveUseCase() } returns successEmptyResult()
            buildSut()

            sut.sendAction(BackupUiActions.Action.DisconnectAction)

            val effect = sut.uiEffects.first()
            assertEquals(BackupUiEffects.DisconnectCompleted, effect)
            val state = sut.uiState.first()
            assertTrue(state is BackupUiState.Overview && state.connectedAccountEmail == null)
        }

    @Test
    fun `GIVEN restore fails with no backup found WHEN RestoreAction is sent THEN a snackbar error effect must be emitted`() =
        runBlockingUnitTest {
            stubStatus(email = "user@gmail.com")
            coEvery { restoreBackupUseCase() } returns failureWith(BackupError.NoBackupFound())
            buildSut()

            sut.sendAction(BackupUiActions.Action.RestoreAction)

            val effect = sut.uiEffects.first()
            assertEquals(
                BackupUiEffects.SnackbarError(R.string.backup_error_no_backup_found),
                effect
            )
        }

    private fun <T> failureWith(exception: Exception): Result<T> =
        Result.failure(exception)
}

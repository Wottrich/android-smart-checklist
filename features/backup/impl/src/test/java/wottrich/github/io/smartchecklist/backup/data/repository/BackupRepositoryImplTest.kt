package wottrich.github.io.smartchecklist.backup.data.repository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.serialization.SerializationException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import wottrich.github.io.smartchecklist.backup.data.BackupPreferencesDatasource
import wottrich.github.io.smartchecklist.backup.data.backupfile.BackupFileModel
import wottrich.github.io.smartchecklist.backup.data.backupfile.ChecklistBackupModel
import wottrich.github.io.smartchecklist.backup.data.mapper.toChecklistsWithTasks
import wottrich.github.io.smartchecklist.backup.data.serializer.BackupFileSerializer
import wottrich.github.io.smartchecklist.backup.data.serializer.BackupFileSerializerException
import wottrich.github.io.smartchecklist.backup.domain.BackupError
import wottrich.github.io.smartchecklist.backup.google.DriveBackupDatasource
import wottrich.github.io.smartchecklist.backup.google.FakeGoogleDriveAuthorization
import wottrich.github.io.smartchecklist.backup.google.GoogleDriveAuthorization
import wottrich.github.io.smartchecklist.backup.google.GoogleDriveBackupConfig
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders
import wottrich.github.io.smartchecklist.datasource.data.datasource.ChecklistDatasource
import wottrich.github.io.smartchecklist.datasource.data.model.Checklist
import wottrich.github.io.smartchecklist.datasource.data.model.ChecklistWithTasks
import wottrich.github.io.smartchecklist.testtools.BaseUnitTest

@OptIn(ExperimentalCoroutinesApi::class)
class BackupRepositoryImplTest : BaseUnitTest() {

    private lateinit var sut: BackupRepositoryImpl

    private val checklistDatasource: ChecklistDatasource = mockk()
    private val driveBackupDatasource: DriveBackupDatasource = mockk()
    private val backupPreferencesDatasource: BackupPreferencesDatasource = mockk(relaxed = true)
    private val backupFileSerializer: BackupFileSerializer = mockk()

    private val grantedOutcome =
        GoogleDriveAuthorization.AuthorizationOutcome.Granted(accessToken = "token", accountEmail = "user@gmail.com")
    private val googleDriveAuthorization = FakeGoogleDriveAuthorization(grantedOutcome)

    @Before
    fun sutUp() {
        googleDriveAuthorization.outcome = grantedOutcome
        sut = BackupRepositoryImpl(
            dispatchersProviders = coroutinesTestRule.dispatchers,
            checklistDatasource = checklistDatasource,
            driveBackupDatasource = driveBackupDatasource,
            googleDriveAuthorization = googleDriveAuthorization,
            backupPreferencesDatasource = backupPreferencesDatasource,
            backupFileSerializer = backupFileSerializer
        )
    }

    @Test
    fun `GIVEN stored preferences WHEN backup status is requested THEN it must return the persisted values`() =
        runBlockingUnitTest {
            coEvery { backupPreferencesDatasource.getConnectedAccountEmail() } returns "user@gmail.com"
            coEvery { backupPreferencesDatasource.getLastBackupDate() } returns 1000L

            val status = sut.getBackupStatus()

            assertEquals("user@gmail.com", status.connectedAccountEmail)
            assertEquals(1000L, status.lastBackupDate)
        }

    @Test
    fun `GIVEN a granted authorization WHEN connect is called THEN the account email must be persisted and returned`() =
        runBlockingUnitTest {
            coEvery { backupPreferencesDatasource.getConnectedAccountEmail() } returns "user@gmail.com"
            coEvery { backupPreferencesDatasource.getLastBackupDate() } returns null

            val result = sut.connect()

            verify(exactly = 1) { backupPreferencesDatasource.setConnectedAccountEmail("user@gmail.com") }
            assertEquals("user@gmail.com", result.getOrNull()?.connectedAccountEmail)
        }

    @Test
    fun `GIVEN a consent requirement WHEN connect is called THEN it must fail with NeedsConsent`() =
        runBlockingUnitTest {
            val pendingIntent = mockk<android.app.PendingIntent>()
            googleDriveAuthorization.outcome =
                GoogleDriveAuthorization.AuthorizationOutcome.NeedsConsent(pendingIntent)

            val result = sut.connect()

            assertTrue(result.exceptionOrNull() is BackupError.NeedsConsent)
        }

    @Test
    fun `GIVEN an authorization failure WHEN connect is called THEN it must fail with DriveIoError`() =
        runBlockingUnitTest {
            googleDriveAuthorization.outcome =
                GoogleDriveAuthorization.AuthorizationOutcome.Failed(java.io.IOException("offline"))

            val result = sut.connect()

            assertTrue(result.exceptionOrNull() is BackupError.DriveIoError)
        }

    @Test
    fun `GIVEN an authorized session WHEN createBackup is called THEN it must write the backup file and stamp the last backup date`() =
        runBlockingUnitTest {
            coEvery { checklistDatasource.getAllChecklistsWithTasks() } returns listOf(checklistWithTasks())
            coEvery { backupFileSerializer.serialize(any()) } returns validSerializedBackup()
            coEvery { driveBackupDatasource.write(any(), any(), any()) } returns Result.success(Unit)

            val result = sut.createBackup()

            assertTrue(result.isSuccess)
            coVerify(exactly = 1) {
                driveBackupDatasource.write("token", GoogleDriveBackupConfig.BACKUP_FILE_NAME, any())
            }
            coVerify(exactly = 1) { backupPreferencesDatasource.setLastBackupDate(any()) }
        }

    @Test
    fun `GIVEN a drive write failure WHEN createBackup is called THEN it must fail with DriveIoError and not stamp the last backup date`() =
        runBlockingUnitTest {
            coEvery { checklistDatasource.getAllChecklistsWithTasks() } returns listOf(checklistWithTasks())
            coEvery { backupFileSerializer.serialize(any()) } returns validSerializedBackup()
            coEvery { driveBackupDatasource.write(any(), any(), any()) } returns
                Result.failure(java.io.IOException("drive down"))

            val result = sut.createBackup()

            assertTrue(result.exceptionOrNull() is BackupError.DriveIoError)
            coVerify(inverse = true) { backupPreferencesDatasource.setLastBackupDate(any()) }
        }

    @Test
    fun `GIVEN no connected account WHEN createBackup is called THEN it must fail with NeedsConsent`() =
        runBlockingUnitTest {
            val pendingIntent = mockk<android.app.PendingIntent>()
            googleDriveAuthorization.outcome =
                GoogleDriveAuthorization.AuthorizationOutcome.NeedsConsent(pendingIntent)

            val result = sut.createBackup()

            assertTrue(result.exceptionOrNull() is BackupError.NeedsConsent)
        }

    @Test
    fun `GIVEN an authorized session WHEN restoreBackup is called THEN it must replace all checklists`() =
        runBlockingUnitTest {
            coEvery { driveBackupDatasource.read(any(), any()) } returns
                Result.success(validSerializedBackup().toByteArray())
            coEvery { backupFileSerializer.deserialize(any()) } returns validBackupFile()
            coEvery { checklistDatasource.replaceAllChecklists(any()) } returns Unit

            val result = sut.restoreBackup()

            assertTrue(result.isSuccess)
            coVerify(exactly = 1) {
                checklistDatasource.replaceAllChecklists(validBackupFile().toChecklistsWithTasks())
            }
        }

    @Test
    fun `GIVEN no backup file on drive WHEN restoreBackup is called THEN it must fail with NoBackupFound`() =
        runBlockingUnitTest {
            coEvery { driveBackupDatasource.read(any(), any()) } returns Result.success(null)

            val result = sut.restoreBackup()

            assertTrue(result.exceptionOrNull() is BackupError.NoBackupFound)
        }

    @Test
    fun `GIVEN a corrupted backup file WHEN restoreBackup is called THEN it must fail with CorruptedBackupFile`() =
        runBlockingUnitTest {
            coEvery { driveBackupDatasource.read(any(), any()) } returns
                Result.success("corrupted".toByteArray())
            coEvery { backupFileSerializer.deserialize(any()) } throws
                BackupFileSerializerException(SerializationException("invalid json"))

            val result = sut.restoreBackup()

            assertTrue(result.exceptionOrNull() is BackupError.CorruptedBackupFile)
            coVerify(inverse = true) { checklistDatasource.replaceAllChecklists(any()) }
        }

    @Test
    fun `GIVEN a drive read failure WHEN restoreBackup is called THEN it must fail with DriveIoError`() =
        runBlockingUnitTest {
            coEvery { driveBackupDatasource.read(any(), any()) } returns
                Result.failure(java.io.IOException("drive down"))

            val result = sut.restoreBackup()

            assertTrue(result.exceptionOrNull() is BackupError.DriveIoError)
        }

    @Test
    fun `GIVEN a connected account WHEN disconnect is called THEN the access must be revoked and preferences cleared`() =
        runBlockingUnitTest {
            val result = sut.disconnect()

            assertTrue(result.isSuccess)
            assertEquals(1, googleDriveAuthorization.revokeAccessCalls)
            verify(exactly = 1) { backupPreferencesDatasource.setConnectedAccountEmail(null) }
            verify(exactly = 1) { backupPreferencesDatasource.setLastBackupDate(null) }
        }

    private fun checklistWithTasks() = ChecklistWithTasks(
        checklist = Checklist(
            uuid = "checklist-uuid",
            parentUuid = null,
            name = "Checklist",
            isSelected = true,
            createdDate = 1000L,
            lastUpdate = 2000L,
        ),
        tasks = listOf(),
        checklistSectionEmbedded = listOf()
    )

    private fun validBackupFile() = BackupFileModel(
        createdAt = 1000L,
        checklists = listOf(
            ChecklistBackupModel(
                uuid = "checklist-uuid",
                parentUuid = null,
                name = "Checklist",
                isSelected = true,
                createdDate = 1000L,
                lastUpdate = 2000L,
                tasks = listOf()
            )
        )
    )

    private fun validSerializedBackup() =
        "{\"schemaVersion\":${BackupFileModel.CURRENT_SCHEMA_VERSION},\"createdAt\":1000,\"checklists\":[]}"
}

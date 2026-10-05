package wottrich.github.io.smartchecklist.backup.google

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.googleapis.json.GoogleJsonResponseException
import com.google.api.client.http.HttpRequestInitializer
import com.google.api.client.http.InputStreamContent
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.drive.Drive
import com.google.api.services.drive.model.File
import kotlinx.coroutines.withContext
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders

/**
 * Google Drive `appDataFolder` file operations, built per operation from a
 * short-lived Bearer access token (no deprecated `GoogleAccountCredential` /
 * `AndroidHttp`).
 */
internal class DriveBackupDatasourceImpl(
    private val dispatchersProviders: DispatchersProviders
) : DriveBackupDatasource {

    override suspend fun write(
        accessToken: String,
        fileName: String,
        content: ByteArray
    ): Result<Unit> = withContext(dispatchersProviders.io) {
        try {
            val drive = buildDrive(accessToken)
            val existingFileId = findFileId(drive, fileName)
            if (existingFileId == null) {
                val metadata = File()
                    .setName(fileName)
                    .setParents(listOf(APP_DATA_FOLDER))
                drive.files()
                    .create(metadata, InputStreamContent(JSON_MIME_TYPE, content.inputStream()))
                    .execute()
            } else {
                drive.files()
                    .update(existingFileId, null, InputStreamContent(JSON_MIME_TYPE, content.inputStream()))
                    .execute()
            }
            Result.success(Unit)
        } catch (exception: Exception) {
            Result.failure(exception)
        }
    }

    override suspend fun read(accessToken: String, fileName: String): Result<ByteArray?> =
        withContext(dispatchersProviders.io) {
            try {
                val drive = buildDrive(accessToken)
                val fileId = findFileId(drive, fileName)
                    ?: return@withContext Result.success(null)
                drive.files()[fileId].executeMediaAsInputStream().use { stream ->
                    Result.success(stream.readBytes())
                }
            } catch (exception: Exception) {
                Result.failure(exception)
            }
        }

    override suspend fun delete(accessToken: String, fileName: String): Result<Unit> =
        withContext(dispatchersProviders.io) {
            try {
                val drive = buildDrive(accessToken)
                val fileId = findFileId(drive, fileName)
                if (fileId != null) {
                    drive.files().delete(fileId).execute()
                }
                Result.success(Unit)
            } catch (exception: Exception) {
                Result.failure(exception)
            }
        }

    /** `null` when the file does not exist in the `appDataFolder` space. */
    private fun findFileId(drive: Drive, fileName: String): String? {
        return try {
            val files = drive.files()
                .list()
                .setQ("name = '$fileName' and '$APP_DATA_FOLDER' in parents")
                .setSpaces(APP_DATA_FOLDER)
                .execute()
            files.files.firstOrNull()?.id
        } catch (expected: GoogleJsonResponseException) {
            // Drive answers 404 for empty appDataFolder listings on fresh accounts.
            if (expected.statusCode == 404) null else throw expected
        }
    }

    private fun buildDrive(accessToken: String): Drive {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
        val requestInitializer = HttpRequestInitializer { httpRequest ->
            httpRequest.headers.authorization = "Bearer $accessToken"
        }
        return Drive.Builder(httpTransport, GsonFactory.getDefaultInstance(), requestInitializer)
            .setApplicationName(GoogleDriveBackupConfig.APPLICATION_NAME)
            .build()
    }

    private companion object {
        const val APP_DATA_FOLDER = "appDataFolder"
        const val JSON_MIME_TYPE = "application/json"
    }
}

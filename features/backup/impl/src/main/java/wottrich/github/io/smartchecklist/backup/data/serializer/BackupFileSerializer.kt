package wottrich.github.io.smartchecklist.backup.data.serializer

import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import wottrich.github.io.smartchecklist.backup.data.backupfile.BackupFileModel

/**
 * JSON boundary of the backup file. Gates on [BackupFileModel.CURRENT_SCHEMA_VERSION]:
 * restoring a file written by a newer schema (or any corrupted content) is refused
 * with [BackupFileSerializerException] instead of silently half-restoring.
 */
interface BackupFileSerializer {

    fun serialize(backupFile: BackupFileModel): String

    /** @throws BackupFileSerializerException on corrupted content or unknown schema version. */
    fun deserialize(content: String): BackupFileModel
}

internal class BackupFileSerializerImpl : BackupFileSerializer {

    private val json = Json {
        ignoreUnknownKeys = false
        encodeDefaults = true
    }

    override fun serialize(backupFile: BackupFileModel): String = json.encodeToString(backupFile)

    override fun deserialize(content: String): BackupFileModel {
        val backupFile = try {
            json.decodeFromString<BackupFileModel>(content)
        } catch (expected: SerializationException) {
            throw BackupFileSerializerException(expected)
        } catch (expected: IllegalArgumentException) {
            throw BackupFileSerializerException(expected)
        }
        if (backupFile.schemaVersion > BackupFileModel.CURRENT_SCHEMA_VERSION) {
            throw BackupFileSerializerException(
                IllegalArgumentException("Backup schema version ${backupFile.schemaVersion} is not supported")
            )
        }
        return backupFile
    }
}

class BackupFileSerializerException(cause: Throwable) : Exception(cause)

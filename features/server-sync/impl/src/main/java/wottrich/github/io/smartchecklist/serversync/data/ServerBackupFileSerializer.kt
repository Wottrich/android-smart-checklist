package wottrich.github.io.smartchecklist.serversync.data

import kotlinx.serialization.SerializationException
import wottrich.github.io.smartchecklist.backup.data.backupfile.BackupFileModel
import wottrich.github.io.smartchecklist.serversync.api.ServerSyncJson

/**
 * Server-side mirror of the backup feature's `BackupFileSerializer` (that one is
 * `internal` to `:features:backup:impl`). Same gates: corrupted content and unknown
 * schema versions are refused instead of half-restored.
 */
internal class ServerBackupFileSerializer {

    fun serialize(backupFile: BackupFileModel): String = ServerSyncJson.encodeToString(
        BackupFileModel.serializer(),
        backupFile
    )

    /** @throws ServerBackupFileSerializerException on corrupted content or unknown schema version. */
    fun deserialize(content: String): BackupFileModel {
        val backupFile = try {
            ServerSyncJson.decodeFromString(BackupFileModel.serializer(), content)
        } catch (expected: SerializationException) {
            throw ServerBackupFileSerializerException(expected)
        } catch (expected: IllegalArgumentException) {
            throw ServerBackupFileSerializerException(expected)
        }
        if (backupFile.schemaVersion > BackupFileModel.CURRENT_SCHEMA_VERSION) {
            throw ServerBackupFileSerializerException(
                IllegalArgumentException("Backup schema version ${backupFile.schemaVersion} is not supported")
            )
        }
        return backupFile
    }
}

class ServerBackupFileSerializerException(cause: Throwable) : Exception(cause)

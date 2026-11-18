package wottrich.github.io.smartchecklist.serversync.data.repository

import wottrich.github.io.smartchecklist.backup.data.repository.BackupRepository
import wottrich.github.io.smartchecklist.serversync.data.ServerSettingsDatasource

/**
 * Phase F flag flip: one `BackupRepository` binding, two backends. Server settings
 * configured → the self-hosted server; otherwise the original Drive impl. Drive
 * code is untouched; users only move to the server by pairing deliberately.
 */
internal class RoutingBackupRepository(
    private val serverSettingsDatasource: ServerSettingsDatasource,
    private val driveBackupRepository: BackupRepository,
    private val serverBackupRepository: BackupRepository,
) : BackupRepository {

    private val active: BackupRepository
        get() = if (serverSettingsDatasource.isConfigured()) {
            serverBackupRepository
        } else {
            driveBackupRepository
        }

    override suspend fun getBackupStatus() = active.getBackupStatus()

    override suspend fun connect() = active.connect()

    override suspend fun disconnect() = active.disconnect()

    override suspend fun createBackup() = active.createBackup()

    override suspend fun restoreBackup() = active.restoreBackup()
}

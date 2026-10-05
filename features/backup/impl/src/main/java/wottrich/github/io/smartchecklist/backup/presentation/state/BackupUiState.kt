package wottrich.github.io.smartchecklist.backup.presentation.state

sealed class BackupUiState {

    data object Loading : BackupUiState()

    data class Overview(
        val connectedAccountEmail: String?,
        val lastBackupDate: Long?,
        val isWorking: Boolean,
    ) : BackupUiState() {

        val isConnected: Boolean get() = connectedAccountEmail != null
    }
}

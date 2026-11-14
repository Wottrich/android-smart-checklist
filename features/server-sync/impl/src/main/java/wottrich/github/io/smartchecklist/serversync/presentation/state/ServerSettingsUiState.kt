package wottrich.github.io.smartchecklist.serversync.presentation.state

sealed class ServerSettingsUiState {

    data object Loading : ServerSettingsUiState()

    data class Overview(
        val isConfigured: Boolean,
        val serverUrl: String?,
        val lastSyncAt: Long?,
        val pendingChanges: Int,
        val lastErrorKind: String?,
        val isWorking: Boolean,
    ) : ServerSettingsUiState()
}

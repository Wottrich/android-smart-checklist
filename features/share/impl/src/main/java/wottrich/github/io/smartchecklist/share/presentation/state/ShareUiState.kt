package wottrich.github.io.smartchecklist.share.presentation.state

sealed class ShareUiState {

    data object Loading : ShareUiState()

    data class Overview(
        val checklistName: String,
        val isShared: Boolean,
        val isHosting: Boolean,
        val deviceName: String,
        /** `ip:port` guests can dial, or null when the host is not running. */
        val guestAddress: String?,
        val guestCount: Int,
        /** Current pairing code (`XXXX-XXXX`) — rotates every 10 minutes (v2). */
        val pairingCode: String,
        /** Millis until [pairingCode] rotates — for the UI countdown. */
        val pairingRotationInMs: Long,
        val isWorking: Boolean,
    ) : ShareUiState()
}

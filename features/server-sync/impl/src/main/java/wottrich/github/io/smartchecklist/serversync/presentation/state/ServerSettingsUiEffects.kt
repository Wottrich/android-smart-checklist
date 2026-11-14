package wottrich.github.io.smartchecklist.serversync.presentation.state

import androidx.annotation.StringRes

sealed class ServerSettingsUiEffects {

    /** One-shot localized message (pairing results, sync results, failures). */
    data class ShowSnackbar(@StringRes val message: Int) : ServerSettingsUiEffects()
}

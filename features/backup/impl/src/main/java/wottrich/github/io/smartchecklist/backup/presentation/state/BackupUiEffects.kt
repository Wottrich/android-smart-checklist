package wottrich.github.io.smartchecklist.backup.presentation.state

import android.app.PendingIntent
import androidx.annotation.StringRes

sealed class BackupUiEffects {

    /** The user must grant the `drive.appdata` consent; launch [resolvablePendingIntent]. */
    data class RequestConsent(val resolvablePendingIntent: PendingIntent) : BackupUiEffects()

    /** One-shot localized message (completed actions and failures alike). */
    data class ShowSnackbar(@StringRes val message: Int) : BackupUiEffects()
}

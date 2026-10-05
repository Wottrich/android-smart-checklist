package wottrich.github.io.smartchecklist.backup.presentation.state

import android.app.PendingIntent
import androidx.annotation.StringRes

sealed class BackupUiEffects {

    /** The user must grant the `drive.appdata` consent; launch [resolvablePendingIntent]. */
    data class RequestConsent(val resolvablePendingIntent: PendingIntent) : BackupUiEffects()

    data object BackupCompleted : BackupUiEffects()

    data object RestoreCompleted : BackupUiEffects()

    data object DisconnectCompleted : BackupUiEffects()

    data class SnackbarError(@StringRes val errorMessage: Int) : BackupUiEffects()
}

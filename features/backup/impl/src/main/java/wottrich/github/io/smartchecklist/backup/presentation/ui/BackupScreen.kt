@file:OptIn(ExperimentalMaterialApi::class)

package wottrich.github.io.smartchecklist.backup.presentation.ui

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.getViewModel
import wottrich.github.io.smartchecklist.backup.R
import wottrich.github.io.smartchecklist.backup.presentation.state.BackupUiActions
import wottrich.github.io.smartchecklist.backup.presentation.state.BackupUiEffects
import wottrich.github.io.smartchecklist.backup.presentation.state.BackupUiState
import wottrich.github.io.smartchecklist.backup.presentation.viewmodel.BackupViewModel
import wottrich.github.io.smartchecklist.baseui.TopBarContent
import wottrich.github.io.smartchecklist.baseui.icons.ArrowBackIcon
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@Composable
fun BackupScreen(onBackPressed: () -> Unit) {
    ApplicationTheme {
        BackupScreenContent(onBackPressed = onBackPressed)
    }
}

@Composable
private fun BackupScreenContent(
    onBackPressed: () -> Unit,
    viewModel: BackupViewModel = getViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current

    // Consent resolution: launching the resolvable pending intent from
    // `AuthorizationClient`. The result data intent must be consumed by the
    // ViewModel (`getAuthorizationResultFromIntent`) — that is where the access
    // token comes from.
    val consentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.sendAction(BackupUiActions.Action.ConsentCompletedAction(result.data))
        } else {
            // GMS reports failures inside the consent flow (client/SHA-1 mismatch,
            // unregistered test user…) as RESULT_CANCELED carrying error extras.
            result.data?.extras?.let { extras ->
                val extrasDescription = extras.keySet().joinToString(", ") { key ->
                    "$key=${extras.get(key)}"
                }
                android.util.Log.w("BackupConsent", "Consent canceled with extras: $extrasDescription")
            }
            viewModel.sendAction(BackupUiActions.Action.ConsentCanceledAction)
        }
    }

    var showRestoreConfirmation by remember { mutableStateOf(false) }
    var showDisconnectConfirmation by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = viewModel.uiEffects) {
        viewModel.uiEffects.collect { effect ->
            when (effect) {
                is BackupUiEffects.RequestConsent -> consentLauncher.launch(
                    IntentSenderRequest.Builder(effect.resolvablePendingIntent.intentSender).build()
                )

                is BackupUiEffects.ShowSnackbar -> scaffoldState.snackbarHostState.showSnackbar(
                    context.getString(effect.message)
                )
            }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopBarContent(
                title = {
                    Text(text = stringResource(id = R.string.backup_screen_title))
                },
                navigationIcon = {
                    ArrowBackIcon()
                },
                navigationIconAction = onBackPressed
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val currentState = state) {
                is BackupUiState.Loading -> CircularProgressIndicator()

                is BackupUiState.Overview -> BackupOverview(
                    state = currentState,
                    onConnect = { viewModel.sendAction(BackupUiActions.Action.ConnectAction) },
                    onDisconnect = { showDisconnectConfirmation = true },
                    onBackupNow = { viewModel.sendAction(BackupUiActions.Action.BackupNowAction) },
                    onRestore = { showRestoreConfirmation = true },
                )
            }
        }
    }

    if (showRestoreConfirmation) {
        BackupConfirmationDialog(
            title = R.string.backup_restore_confirmation_title,
            message = R.string.backup_restore_confirmation_message,
            positiveLabel = R.string.backup_restore_confirmation_positive,
            onPositive = {
                showRestoreConfirmation = false
                viewModel.sendAction(BackupUiActions.Action.RestoreAction)
            },
            onNegative = { showRestoreConfirmation = false }
        )
    }

    if (showDisconnectConfirmation) {
        BackupConfirmationDialog(
            title = R.string.backup_disconnect_confirmation_title,
            message = R.string.backup_disconnect_confirmation_message,
            positiveLabel = R.string.backup_disconnect_confirmation_positive,
            onPositive = {
                showDisconnectConfirmation = false
                viewModel.sendAction(BackupUiActions.Action.DisconnectAction)
            },
            onNegative = { showDisconnectConfirmation = false }
        )
    }
}

@Composable
private fun BackupOverview(
    state: BackupUiState.Overview,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onBackupNow: () -> Unit,
    onRestore: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        AccountContent(
            state = state,
            onConnect = onConnect,
            onDisconnect = onDisconnect
        )
        Divider(modifier = Modifier.fillMaxWidth())
        BackupNowContent(
            state = state,
            onBackupNow = onBackupNow
        )
        Divider(modifier = Modifier.fillMaxWidth())
        RestoreContent(
            state = state,
            onRestore = onRestore
        )
        Divider(modifier = Modifier.fillMaxWidth())
        Text(
            modifier = Modifier.padding(all = Dimens.BaseFour.SizeThree),
            text = stringResource(id = R.string.backup_stored_in_drive_app_data),
            color = SmartChecklistTheme.colors.onBackground
        )
        if (state.isWorking) {
            CircularProgressIndicator(
                modifier = Modifier.padding(all = Dimens.BaseFour.SizeThree)
            )
        }
    }
}

@Composable
private fun AccountContent(
    state: BackupUiState.Overview,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
) {
    if (!state.isConnected) {
        ListItem(
            text = {
                Text(text = stringResource(id = R.string.backup_connect_google_drive))
            },
            trailing = {
                Button(onClick = onConnect) {
                    Text(text = stringResource(id = R.string.backup_connect))
                }
            }
        )
    } else {
        val accountLabel = state.connectedAccountEmail
            ?.let { stringResource(id = R.string.backup_connected_account, it) }
            ?: stringResource(id = R.string.backup_connected)
        ListItem(
            text = {
                Text(text = accountLabel)
            },
            trailing = {
                TextButton(onClick = onDisconnect) {
                    Text(text = stringResource(id = R.string.backup_disconnect))
                }
            }
        )
    }
}

@Composable
private fun BackupNowContent(
    state: BackupUiState.Overview,
    onBackupNow: () -> Unit,
) {
    val lastBackupLabel = state.lastBackupDate?.let { lastBackupDate ->
        stringResource(id = R.string.backup_last_backup_date, formatAsDate(lastBackupDate))
    } ?: stringResource(id = R.string.backup_never_backed_up)

    ListItem(
        text = {
            Text(text = stringResource(id = R.string.backup_backup_now))
        },
        secondaryText = {
            Text(text = lastBackupLabel)
        },
        trailing = {
            TextButton(
                onClick = onBackupNow,
                enabled = state.isConnected && !state.isWorking
            ) {
                Text(text = stringResource(id = R.string.backup_backup_now))
            }
        }
    )
}

@Composable
private fun RestoreContent(
    state: BackupUiState.Overview,
    onRestore: () -> Unit,
) {
    ListItem(
        text = {
            Text(text = stringResource(id = R.string.backup_restore))
        },
        trailing = {
            TextButton(
                onClick = onRestore,
                enabled = state.isConnected && !state.isWorking
            ) {
                Text(text = stringResource(id = R.string.backup_restore))
            }
        }
    )
}

@Composable
private fun BackupConfirmationDialog(
    title: Int,
    message: Int,
    positiveLabel: Int,
    onPositive: () -> Unit,
    onNegative: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onNegative,
        title = { Text(text = stringResource(id = title)) },
        text = { Text(text = stringResource(id = message)) },
        confirmButton = {
            TextButton(onClick = onPositive) {
                Text(text = stringResource(id = positiveLabel))
            }
        },
        dismissButton = {
            TextButton(onClick = onNegative) {
                Text(text = stringResource(id = R.string.backup_dialog_negative))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun BackupScreenPreview() {
    ApplicationTheme {
        BackupOverview(
            state = BackupUiState.Overview(
                isConnected = true,
                connectedAccountEmail = "user@gmail.com",
                lastBackupDate = System.currentTimeMillis(),
                isWorking = false
            ),
            onConnect = {},
            onDisconnect = {},
            onBackupNow = {},
            onRestore = {},
        )
    }
}

private fun formatAsDate(epochMillis: Long): String {
    val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
    return dateFormat.format(Date(epochMillis))
}

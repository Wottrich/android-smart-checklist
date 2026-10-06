package wottrich.github.io.smartchecklist.backup.presentation.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
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
import org.koin.androidx.compose.getViewModel

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
    val snackbarHostState = remember { SnackbarHostState() }
    val context = androidx.compose.ui.platform.LocalContext.current

    // Consent resolution: launching the resolvable pending intent from
    // `AuthorizationClient`. The result data intent must be consumed by the
    // ViewModel (`getAuthorizationResultFromIntent`) — that is where the access
    // token comes from.
    val consentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            viewModel.sendAction(BackupUiActions.Action.ConsentCompletedAction(result.data))
        }
    }

    var showRestoreConfirmation by remember { mutableStateOf(false) }
    var showDisconnectConfirmation by remember { mutableStateOf(false) }

    val backupCompletedMessage = stringResource(id = R.string.backup_backup_completed)
    val restoreCompletedMessage = stringResource(id = R.string.backup_restore_completed)
    val disconnectedMessage = stringResource(id = R.string.backup_disconnected)

    LaunchedEffect(key1 = viewModel.uiEffects) {
        viewModel.uiEffects.collect { effect ->
            when (effect) {
                is BackupUiEffects.RequestConsent -> consentLauncher.launch(
                    androidx.activity.result.IntentSenderRequest.Builder(
                        effect.resolvablePendingIntent.intentSender
                    ).build()
                )
                is BackupUiEffects.BackupCompleted -> snackbarHostState.showSnackbar(backupCompletedMessage)
                is BackupUiEffects.RestoreCompleted -> snackbarHostState.showSnackbar(restoreCompletedMessage)
                is BackupUiEffects.DisconnectCompleted -> snackbarHostState.showSnackbar(disconnectedMessage)
                is BackupUiEffects.SnackbarError -> snackbarHostState.showSnackbar(
                    context.getString(effect.errorMessage)
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
        },
        snackbarHost = { hostState ->
            SnackbarHost(hostState = hostState) { snackbarData ->
                Snackbar(snackbarData = snackbarData)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            BackupOverviewContent(
                state = state,
                onConnect = { viewModel.sendAction(BackupUiActions.Action.ConnectAction) },
                onDisconnect = { showDisconnectConfirmation = true },
                onBackupNow = { viewModel.sendAction(BackupUiActions.Action.BackupNowAction) },
                onRestore = { showRestoreConfirmation = true },
            )
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
private fun BackupOverviewContent(
    state: BackupUiState,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onBackupNow: () -> Unit,
    onRestore: () -> Unit,
) {
    when (state) {
        is BackupUiState.Loading -> CircularProgressIndicator()

        is BackupUiState.Overview -> BackupOverview(
            state = state,
            onConnect = onConnect,
            onDisconnect = onDisconnect,
            onBackupNow = onBackupNow,
            onRestore = onRestore,
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AccountContent(
    state: BackupUiState.Overview,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
) {
    val connectedAccountEmail = state.connectedAccountEmail
    if (connectedAccountEmail == null) {
        ListItem(
            text = {
                Text(text = stringResource(id = R.string.backup_connect_google_drive))
            },
            trailing = {
                Button(
                    onClick = onConnect,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = SmartChecklistTheme.colors.primary,
                        contentColor = SmartChecklistTheme.colors.onPrimary
                    )
                ) {
                    Text(text = stringResource(id = R.string.backup_connect_google_drive))
                }
            }
        )
    } else {
        ListItem(
            text = {
                Text(text = stringResource(id = R.string.backup_connected_account, connectedAccountEmail))
            },
            trailing = {
                TextButton(onClick = onDisconnect) {
                    Text(text = stringResource(id = R.string.backup_disconnect))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun BackupNowContent(
    state: BackupUiState.Overview,
    onBackupNow: () -> Unit,
) {
    val lastBackupLabel = state.lastBackupDate?.let { lastBackupDate ->
        stringResource(id = R.string.backup_last_backup_date, lastBackupDate.formatAsDate())
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

@OptIn(ExperimentalMaterialApi::class)
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

@OptIn(ExperimentalMaterialApi::class)
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

private fun Long.formatAsDate(): String {
    val dateFormat = java.text.DateFormat.getDateInstance(
        java.text.DateFormat.MEDIUM,
        java.util.Locale.getDefault()
    )
    return dateFormat.format(java.util.Date(this))
}

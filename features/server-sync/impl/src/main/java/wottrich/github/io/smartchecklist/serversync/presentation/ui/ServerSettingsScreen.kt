@file:OptIn(ExperimentalMaterialApi::class)

package wottrich.github.io.smartchecklist.serversync.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Divider
import androidx.compose.material.ListItem
import androidx.compose.material.OutlinedTextField
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
import wottrich.github.io.smartchecklist.baseui.TopBarContent
import wottrich.github.io.smartchecklist.baseui.icons.ArrowBackIcon
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.serversync.R
import wottrich.github.io.smartchecklist.serversync.presentation.state.ServerSettingsUiActions
import wottrich.github.io.smartchecklist.serversync.presentation.state.ServerSettingsUiEffects
import wottrich.github.io.smartchecklist.serversync.presentation.state.ServerSettingsUiState
import wottrich.github.io.smartchecklist.serversync.presentation.viewmodel.ServerSettingsViewModel
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ServerSettingsScreen(onBackPressed: () -> Unit) {
    ApplicationTheme {
        ServerSettingsScreenContent(onBackPressed = onBackPressed)
    }
}

@Composable
private fun ServerSettingsScreenContent(
    onBackPressed: () -> Unit,
    viewModel: ServerSettingsViewModel = getViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val scaffoldState = rememberScaffoldState()
    val context = LocalContext.current

    LaunchedEffect(key1 = viewModel.uiEffects) {
        viewModel.uiEffects.collect { effect ->
            when (effect) {
                is ServerSettingsUiEffects.ShowSnackbar ->
                    scaffoldState.snackbarHostState.showSnackbar(context.getString(effect.message))
            }
        }
    }

    var showDisconnectConfirmation by remember { mutableStateOf(false) }

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopBarContent(
                title = {
                    Text(text = stringResource(id = R.string.server_sync_screen_title))
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
                is ServerSettingsUiState.Loading -> CircularProgressIndicator()

                is ServerSettingsUiState.Overview -> ServerSettingsOverview(
                    state = currentState,
                    onRegister = { url ->
                        viewModel.sendAction(ServerSettingsUiActions.Action.RegisterAction(url))
                    },
                    onTestConnection = {
                        viewModel.sendAction(ServerSettingsUiActions.Action.TestConnectionAction)
                    },
                    onDisconnect = { showDisconnectConfirmation = true },
                    onSyncNow = {
                        viewModel.sendAction(ServerSettingsUiActions.Action.SyncNowAction)
                    },
                )
            }
        }
    }

    if (showDisconnectConfirmation) {
        ServerSettingsConfirmationDialog(
            title = R.string.server_sync_disconnect_confirmation_title,
            message = R.string.server_sync_disconnect_confirmation_message,
            positiveLabel = R.string.server_sync_disconnect_confirmation_positive,
            onPositive = {
                showDisconnectConfirmation = false
                viewModel.sendAction(ServerSettingsUiActions.Action.DisconnectAction)
            },
            onNegative = { showDisconnectConfirmation = false }
        )
    }
}

@Composable
private fun ServerSettingsOverview(
    state: ServerSettingsUiState.Overview,
    onRegister: (String) -> Unit,
    onTestConnection: () -> Unit,
    onDisconnect: () -> Unit,
    onSyncNow: () -> Unit,
) {
    var serverUrlInput by remember(state.serverUrl) { mutableStateOf(state.serverUrl.orEmpty()) }

    Column(modifier = Modifier.fillMaxWidth()) {
        PairingContent(
            state = state,
            serverUrlInput = serverUrlInput,
            onServerUrlInputChange = { serverUrlInput = it },
            onRegister = { onRegister(serverUrlInput) },
            onTestConnection = onTestConnection,
            onDisconnect = onDisconnect
        )
        Divider(modifier = Modifier.fillMaxWidth())
        SyncStatusContent(state = state, onSyncNow = onSyncNow)
        if (state.isWorking) {
            CircularProgressIndicator(
                modifier = Modifier.padding(all = Dimens.BaseFour.SizeThree)
            )
        }
    }
}

@Composable
private fun PairingContent(
    state: ServerSettingsUiState.Overview,
    serverUrlInput: String,
    onServerUrlInputChange: (String) -> Unit,
    onRegister: () -> Unit,
    onTestConnection: () -> Unit,
    onDisconnect: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.BaseFour.SizeThree),
            value = serverUrlInput,
            onValueChange = onServerUrlInputChange,
            enabled = !state.isConfigured && !state.isWorking,
            label = { Text(text = stringResource(id = R.string.server_sync_server_url_label)) },
            placeholder = { Text(text = stringResource(id = R.string.server_sync_server_url_placeholder)) },
            singleLine = true
        )
        ListItem(
            text = {
                Text(
                    text = state.serverUrl
                        ?: stringResource(id = R.string.server_sync_connection_not_configured)
                )
            },
            trailing = {
                if (state.isConfigured) {
                    TextButton(onClick = onDisconnect, enabled = !state.isWorking) {
                        Text(text = stringResource(id = R.string.server_sync_disconnect))
                    }
                } else {
                    Button(onClick = onRegister, enabled = !state.isWorking) {
                        Text(text = stringResource(id = R.string.server_sync_pair))
                    }
                }
            }
        )
        if (state.isConfigured) {
            ListItem(
                text = { Text(text = stringResource(id = R.string.server_sync_test_connection)) },
                trailing = {
                    TextButton(onClick = onTestConnection, enabled = !state.isWorking) {
                        Text(text = stringResource(id = R.string.server_sync_test_connection))
                    }
                }
            )
        }
    }
}

@Composable
private fun SyncStatusContent(
    state: ServerSettingsUiState.Overview,
    onSyncNow: () -> Unit,
) {
    val lastSyncLabel = state.lastSyncAt
        ?.let { stringResource(id = R.string.server_sync_status_last_sync, formatAsDate(it)) }
        ?: stringResource(id = R.string.server_sync_status_never_synced)

    Column(modifier = Modifier.fillMaxWidth()) {
        ListItem(
            text = { Text(text = stringResource(id = R.string.server_sync_sync_now)) },
            secondaryText = {
                Text(
                    text = buildString {
                        append(lastSyncLabel)
                        append(" · ")
                        append(
                            stringResource(
                                id = R.string.server_sync_status_pending,
                                state.pendingChanges
                            )
                        )
                        state.lastErrorKind?.let {
                            append(" · ")
                            append(stringResource(id = R.string.server_sync_status_last_error, it))
                        }
                    }
                )
            },
            trailing = {
                TextButton(onClick = onSyncNow, enabled = state.isConfigured && !state.isWorking) {
                    Text(text = stringResource(id = R.string.server_sync_sync_now))
                }
            }
        )
    }
}

@Composable
private fun ServerSettingsConfirmationDialog(
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
                Text(text = stringResource(id = R.string.server_sync_dialog_negative))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun ServerSettingsScreenPreview() {
    ApplicationTheme {
        ServerSettingsOverview(
            state = ServerSettingsUiState.Overview(
                isConfigured = true,
                serverUrl = "http://192.168.0.10:8080",
                lastSyncAt = System.currentTimeMillis(),
                pendingChanges = 3,
                lastErrorKind = null,
                isWorking = false
            ),
            onRegister = {},
            onTestConnection = {},
            onDisconnect = {},
            onSyncNow = {},
        )
    }
}

private fun formatAsDate(epochMillis: Long): String {
    val dateFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault())
    return dateFormat.format(Date(epochMillis))
}

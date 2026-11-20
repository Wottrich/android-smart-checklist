package wottrich.github.io.smartchecklist.share.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.getViewModel
import wottrich.github.io.smartchecklist.baseui.TopBarContent
import wottrich.github.io.smartchecklist.baseui.components.ButtonVariant
import wottrich.github.io.smartchecklist.baseui.components.SmartChecklistButton
import wottrich.github.io.smartchecklist.baseui.icons.ArrowBackIcon
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.share.R
import wottrich.github.io.smartchecklist.share.presentation.state.ShareUiActions
import wottrich.github.io.smartchecklist.share.presentation.state.ShareUiEffects
import wottrich.github.io.smartchecklist.share.presentation.state.ShareUiState
import wottrich.github.io.smartchecklist.share.presentation.viewmodel.ShareViewModel

/**
 * Owner screen for Wi-Fi sharing (Phase D): flag/unflag the selected checklist,
 * show hosting status (device name, guest address, guests) and stop sharing with
 * the frozen-copy confirmation.
 */
@Composable
fun ShareScreen(
    onBackPressed: () -> Unit,
    viewModel: ShareViewModel = getViewModel()
) {
    ApplicationTheme {
        val state by viewModel.uiState.collectAsState()
        val scaffoldState = rememberScaffoldState()
        Effects(viewModel = viewModel, snackbarHostState = scaffoldState.snackbarHostState)
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopBarContent(
                    title = { Text(stringResource(id = R.string.share_screen_title)) },
                    navigationIcon = { ArrowBackIcon() },
                    navigationIconAction = onBackPressed,
                )
            }
        ) { padding ->
            when (val current = state) {
                ShareUiState.Loading -> Unit
                is ShareUiState.Overview -> ShareContent(
                    state = current,
                    onStartSharing = {
                        viewModel.sendAction(ShareUiActions.Action.StartSharingAction)
                    },
                    onStopSharing = {
                        viewModel.sendAction(ShareUiActions.Action.StopSharingAction)
                    },
                    onResetPairing = {
                        viewModel.sendAction(ShareUiActions.Action.ResetPairingAction)
                    },
                    modifier = Modifier.padding(padding),
                )
            }
        }
    }
}

@Composable
private fun Effects(
    viewModel: ShareViewModel,
    snackbarHostState: SnackbarHostState,
) {
    val effects = viewModel.uiEffects
    val context = LocalContext.current
    LaunchedEffect(key1 = effects) {
        effects.collect { effect ->
            when (effect) {
                is ShareUiEffects.ShowSnackbar ->
                    snackbarHostState.showSnackbar(context.getString(effect.textRes))
            }
        }
    }
}

@Composable
private fun ShareContent(
    state: ShareUiState.Overview,
    onStartSharing: () -> Unit,
    onStopSharing: () -> Unit,
    onResetPairing: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showStopConfirmation by remember { mutableStateOf(false) }
    if (showStopConfirmation) {
        StopSharingDialog(
            onConfirm = {
                showStopConfirmation = false
                onStopSharing()
            },
            onDismiss = { showStopConfirmation = false },
        )
    }
    Column(modifier = modifier.padding(Dimens.BaseFour.SizeTwo)) {
        Text(
            text = state.checklistName,
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(vertical = Dimens.BaseFour.SizeTwo),
        )
        if (state.isShared) {
            SharingStatusContent(state)
            PairingCodeCard(
                code = state.pairingCode,
                rotationInMs = state.pairingRotationInMs,
                onResetPairing = onResetPairing,
                enabled = !state.isWorking,
            )
            SmartChecklistButton(
                onClick = { showStopConfirmation = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.BaseFour.SizeTwo),
                buttonVariant = ButtonVariant.NEGATIVE,
                enabled = !state.isWorking,
            ) {
                Text(text = stringResource(id = R.string.share_stop_button))
            }
        } else {
            Text(
                text = stringResource(id = R.string.share_explanation),
                modifier = Modifier.padding(vertical = Dimens.BaseFour.SizeTwo),
            )
            SmartChecklistButton(
                onClick = onStartSharing,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = Dimens.BaseFour.SizeTwo),
                enabled = !state.isWorking,
            ) {
                Text(text = stringResource(id = R.string.share_start_button))
            }
        }
    }
}

@Composable
private fun PairingCodeCard(
    code: String,
    rotationInMs: Long,
    onResetPairing: () -> Unit,
    enabled: Boolean,
) {
    Column(modifier = Modifier.padding(top = Dimens.BaseFour.SizeTwo)) {
        Text(text = stringResource(id = R.string.pair_code_label))
        Text(
            text = code,
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(vertical = Dimens.BaseFour.SizeTwo),
        )
        Text(
            text = stringResource(id = R.string.pair_code_rotation_hint, rotationInMs / 60_000 + 1),
        )
        TextButton(onClick = onResetPairing, enabled = enabled) {
            Text(text = stringResource(id = R.string.pair_reset_button))
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SharingStatusContent(state: ShareUiState.Overview) {
    StatusRow(
        label = stringResource(id = R.string.share_status_label),
        value = stringResource(
            id = if (state.isHosting) R.string.share_status_hosting else R.string.share_status_offline
        ),
    )
    StatusRow(
        label = stringResource(id = R.string.share_device_label),
        value = state.deviceName,
    )
    StatusRow(
        label = stringResource(id = R.string.share_address_label),
        value = state.guestAddress ?: stringResource(id = R.string.share_address_unavailable),
    )
    StatusRow(
        label = stringResource(id = R.string.share_guests_label),
        value = state.guestCount.toString(),
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun StatusRow(label: String, value: String) {
    ListItem(
        text = { Text(text = label) },
        secondaryText = { Text(text = value) },
    )
}

@Composable
private fun StopSharingDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.share_stop_confirm_title)) },
        text = { Text(text = stringResource(id = R.string.share_stop_confirm_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(text = stringResource(id = R.string.share_stop_confirm_positive))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.share_dialog_negative))
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun ShareContentNotSharedPreview() {
    ApplicationTheme {
        ShareContent(
            state = ShareUiState.Overview(
                checklistName = "Groceries",
                isShared = false,
                isHosting = false,
                deviceName = "Pixel 8",
                guestAddress = null,
                guestCount = 0,
                pairingCode = "K7RM2-QP4X",
                pairingRotationInMs = 420_000L,
                isWorking = false,
            ),
            onStartSharing = {},
            onStopSharing = {},
            onResetPairing = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ShareContentSharedPreview() {
    ApplicationTheme {
        ShareContent(
            state = ShareUiState.Overview(
                checklistName = "Groceries",
                isShared = true,
                isHosting = true,
                deviceName = "Pixel 8",
                guestAddress = "192.168.0.10:8090",
                guestCount = 1,
                pairingCode = "K7RM2-QP4X",
                pairingRotationInMs = 420_000L,
                isWorking = false,
            ),
            onStartSharing = {},
            onStopSharing = {},
            onResetPairing = {},
        )
    }
}

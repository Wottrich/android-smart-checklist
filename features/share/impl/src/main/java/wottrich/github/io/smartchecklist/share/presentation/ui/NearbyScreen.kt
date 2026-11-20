package wottrich.github.io.smartchecklist.share.presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ListItem
import androidx.compose.material.OutlinedTextField
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
import wottrich.github.io.smartchecklist.baseui.components.SmartChecklistButton
import wottrich.github.io.smartchecklist.baseui.icons.ArrowBackIcon
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.share.R
import wottrich.github.io.smartchecklist.share.api.NearbyHost
import wottrich.github.io.smartchecklist.share.api.ShareOffer
import wottrich.github.io.smartchecklist.share.presentation.state.NearbyUiActions
import wottrich.github.io.smartchecklist.share.presentation.state.NearbyUiEffects
import wottrich.github.io.smartchecklist.share.presentation.state.NearbyUiState
import wottrich.github.io.smartchecklist.share.presentation.viewmodel.NearbyViewModel

/**
 * Guest screen (Phase E): hosts discovered on the Wi-Fi (plus manual entries),
 * a host's share offers and per-checklist accept. Manual `ip:port` entry covers
 * APs that block NSD.
 */
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NearbyScreen(
    onBackPressed: () -> Unit,
    viewModel: NearbyViewModel = getViewModel()
) {
    ApplicationTheme {
        val state by viewModel.uiState.collectAsState()
        val scaffoldState = rememberScaffoldState()
        Effects(viewModel = viewModel, snackbarHostState = scaffoldState.snackbarHostState)
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopBarContent(
                    title = { Text(stringResource(id = R.string.nearby_screen_title)) },
                    navigationIcon = { ArrowBackIcon() },
                    navigationIconAction = onBackPressed,
                )
            }
        ) { padding ->
            when (val current = state) {
                NearbyUiState.Loading -> Unit
                is NearbyUiState.Overview -> NearbyContent(
                    state = current,
                    onScan = { viewModel.sendAction(NearbyUiActions.Action.ScanAction) },
                    onSelectHost = { viewModel.sendAction(NearbyUiActions.Action.SelectHostAction(it)) },
                    onAccept = { offer, host ->
                        viewModel.sendAction(NearbyUiActions.Action.AcceptAction(offer, host))
                    },
                    onAddManualHost = { viewModel.sendAction(NearbyUiActions.Action.AddManualHostAction(it)) },
                    onPair = { host, code ->
                        viewModel.sendAction(NearbyUiActions.Action.PairAction(host, code))
                    },
                    onDismissPairing = { viewModel.dismissPairing() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                )
            }
        }
    }
}

@Composable
private fun Effects(
    viewModel: NearbyViewModel,
    snackbarHostState: SnackbarHostState,
) {
    val effects = viewModel.uiEffects
    val context = LocalContext.current
    LaunchedEffect(key1 = effects) {
        effects.collect { effect ->
            when (effect) {
                is NearbyUiEffects.ShowSnackbar ->
                    snackbarHostState.showSnackbar(context.getString(effect.textRes))
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun NearbyContent(
    state: NearbyUiState.Overview,
    onScan: () -> Unit,
    onSelectHost: (NearbyHost) -> Unit,
    onAccept: (ShareOffer, NearbyHost) -> Unit,
    onAddManualHost: (String) -> Unit,
    onPair: (NearbyHost, String) -> Unit,
    onDismissPairing: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showManualHostDialog by remember { mutableStateOf(false) }
    if (showManualHostDialog) {
        ManualHostDialog(
            onConfirm = { address ->
                showManualHostDialog = false
                onAddManualHost(address)
            },
            onDismiss = { showManualHostDialog = false },
        )
    }
    state.pairingHost?.let { pairingHost ->
        PairingCodeDialog(
            onConfirm = { code ->
                onPair(pairingHost, code)
            },
            onDismiss = onDismissPairing,
        )
    }
    Column(modifier = modifier) {
        SmartChecklistButton(
            onClick = onScan,
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.BaseFour.SizeTwo),
            enabled = !state.isScanning,
        ) {
            Text(text = stringResource(id = if (state.isScanning) R.string.nearby_scanning else R.string.nearby_scan_button))
        }
        TextButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.BaseFour.SizeTwo),
            onClick = { showManualHostDialog = true },
        ) {
            Text(text = stringResource(id = R.string.nearby_add_manual_host))
        }
        if (state.hosts.isEmpty() && !state.isScanning) {
            Text(
                text = stringResource(id = R.string.nearby_empty),
                modifier = Modifier.padding(Dimens.BaseFour.SizeTwo),
            )
        }
        state.hosts.forEach { host ->
            ListItem(
                modifier = Modifier.clickable { onSelectHost(host) },
                text = { Text(text = host.address) },
                secondaryText = {
                    Text(
                        text = stringResource(
                            id = if (host.isManual) R.string.nearby_host_manual else R.string.nearby_host_discovered
                        )
                    )
                },
            )
        }
        if (state.isLoadingOffers) {
            CircularProgressIndicator(
                modifier = Modifier.padding(Dimens.BaseFour.SizeTwo),
            )
        }
        state.offers.forEach { offer ->
            val selectedHost = state.selectedHost ?: return@forEach
            OfferItem(
                offer = offer,
                isAccepting = state.acceptingShareId == offer.shareId,
                onAccept = { onAccept(offer, selectedHost) },
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun OfferItem(
    offer: ShareOffer,
    isAccepting: Boolean,
    onAccept: () -> Unit,
) {
    ListItem(
        text = { Text(text = offer.name) },
        secondaryText = {
            Text(text = stringResource(id = R.string.nearby_offer_owner, offer.ownerDeviceName))
        },
        trailing = {
            if (isAccepting) {
                CircularProgressIndicator()
            } else {
                TextButton(onClick = onAccept) {
                    Text(text = stringResource(id = R.string.nearby_accept))
                }
            }
        },
    )
}

@Composable
private fun PairingCodeDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var code by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.pair_dialog_title)) },
        text = {
            Column {
                Text(text = stringResource(id = R.string.pair_dialog_message))
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text(text = stringResource(id = R.string.pair_code_hint)) },
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(code) }, enabled = code.isNotBlank()) {
                Text(text = stringResource(id = R.string.pair_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.share_dialog_negative))
            }
        },
    )
}

@Composable
private fun ManualHostDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var address by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.nearby_manual_title)) },
        text = {
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text(text = stringResource(id = R.string.nearby_manual_hint)) },
                singleLine = true,
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(address) }, enabled = address.isNotBlank()) {
                Text(text = stringResource(id = R.string.nearby_manual_confirm))
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
private fun NearbyContentPreview() {
    ApplicationTheme {
        NearbyContent(
            state = NearbyUiState.Overview(
                isScanning = false,
                hosts = listOf(NearbyHost(deviceId = "device-owner", address = "192.168.0.10:8090")),
                selectedHost = NearbyHost(deviceId = "device-owner", address = "192.168.0.10:8090"),
                pairingHost = null,
                offers = listOf(
                    ShareOffer(shareId = "s-1", name = "Groceries", ownerDeviceName = "Pixel 8", updatedAt = 0L),
                ),
                isLoadingOffers = false,
                acceptingShareId = null,
                isWorking = false,
            ),
            onScan = {},
            onSelectHost = {},
            onAccept = { _, _ -> },
            onAddManualHost = {},
            onPair = { _, _ -> },
            onDismissPairing = {},
        )
    }
}

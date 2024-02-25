package wottrich.github.io.smartchecklist.presentation.ui.checklistsettings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.ListItem
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import org.koin.androidx.compose.getViewModel
import org.koin.compose.koinInject
import wottrich.github.io.smartchecklist.R
import wottrich.github.io.smartchecklist.baseui.TopBarContent
import wottrich.github.io.smartchecklist.baseui.components.ButtonVariant
import wottrich.github.io.smartchecklist.baseui.components.SmartChecklistButton
import wottrich.github.io.smartchecklist.baseui.icons.ArrowBackIcon
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.intent.navigation.ShareIntentTextNavigator
import wottrich.github.io.smartchecklist.presentation.viewmodel.ChecklistSettingUiEffect
import wottrich.github.io.smartchecklist.presentation.viewmodel.ChecklistSettingUiEffect.CloseScreen
import wottrich.github.io.smartchecklist.presentation.viewmodel.ChecklistSettingsViewModel


@Composable
fun ChecklistSettingsScreen(
    onCloseScreen: () -> Unit,
    onDeleteChecklist: () -> Unit,
    shareIntentTextNavigator: ShareIntentTextNavigator = koinInject()
) {
    ApplicationTheme {
        ScreenAndEffects(
            onCloseScreen,
            onDeleteChecklist = onDeleteChecklist,
            onShareChecklistAsText = {
                shareIntentTextNavigator.shareIntentText(it)
            }
        )
    }
}

@Composable
private fun ScreenAndEffects(
    onCloseScreen: () -> Unit,
    onDeleteChecklist: () -> Unit,
    onShareChecklistAsText: (String) -> Unit,
    viewModel: ChecklistSettingsViewModel = getViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    Effects(
        viewModel = viewModel,
        snackbarHostState = scaffoldState.snackbarHostState,
        onCloseScreen = onCloseScreen,
        onShareChecklistAsText = onShareChecklistAsText
    )
    ScreenScaffold(
        scaffoldState = scaffoldState,
        onCopyChecklist = { viewModel.onCopyChecklistClicked() },
        onShareChecklist = { viewModel.onShareChecklistClicked() },
        onBackButton = onCloseScreen,
        onDeleteChecklist = onDeleteChecklist
    )
}

@Composable
private fun Effects(
    viewModel: ChecklistSettingsViewModel,
    snackbarHostState: SnackbarHostState,
    onCloseScreen: () -> Unit,
    onShareChecklistAsText: (String) -> Unit
) {
    val effects = viewModel.uiEffect
    val context = LocalContext.current
    LaunchedEffect(key1 = effects) {
        effects.collect {
            when (it) {
                CloseScreen -> onCloseScreen()
                is ChecklistSettingUiEffect.ShareChecklistAsText -> onShareChecklistAsText(it.text)
                is ChecklistSettingUiEffect.SnackbarError -> {
                    snackbarHostState.showSnackbar(context.getString(it.textRes))
                }
            }
        }
    }
}

@Composable
private fun ScreenScaffold(
    scaffoldState: ScaffoldState,
    onCopyChecklist: () -> Unit,
    onShareChecklist: () -> Unit,
    onBackButton: () -> Unit,
    onDeleteChecklist: () -> Unit,
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopBarContent(
                title = {
                    Text(stringResource(id = R.string.checklist_settings_screen_title))
                },
                navigationIcon = {
                    ArrowBackIcon()
                },
                navigationIconAction = onBackButton
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(it)
        ) {
            SettingsComponent(
                onCopyChecklist = onCopyChecklist,
                onShareChecklist = onShareChecklist,
                onDeleteChecklist = onDeleteChecklist
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ColumnScope.SettingsComponent(
    onCopyChecklist: () -> Unit,
    onShareChecklist: () -> Unit,
    onDeleteChecklist: () -> Unit
) {
    Column(
        modifier = Modifier
            .weight(1f)
            .padding(top = Dimens.BaseFour.SizeTwo)
    ) {
        ListItem(
            modifier = Modifier.clickable(onClick = onCopyChecklist),
            text = {
                Text(text = stringResource(id = R.string.checklist_settings_copy_checklist_title))
            },
            secondaryText = {
                Text(text = stringResource(id = R.string.checklist_settings_copy_checklist_subtitle))
            },
            trailing = {
                Icon(imageVector = Icons.Default.Send, contentDescription = null)
            }
        )
        ListItem(
            modifier = Modifier.clickable(onClick = onShareChecklist),
            text = {
                Text(text = stringResource(id = R.string.checklist_settings_share_checklist_title))
            },
            secondaryText = {
                Text(text = stringResource(id = R.string.checklist_settings_share_checklist_subtitle))
            },
            trailing = {
                Icon(imageVector = Icons.Default.Share, contentDescription = null)
            }
        )
    }

    SmartChecklistButton(
        onClick = onDeleteChecklist,
        modifier = Modifier.fillMaxWidth(),
        buttonVariant = ButtonVariant.NEGATIVE
    ) {
        Text(text = stringResource(id = R.string.checklist_delete_label))
    }
}
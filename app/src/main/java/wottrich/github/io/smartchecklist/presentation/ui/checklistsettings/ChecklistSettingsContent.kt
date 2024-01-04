package wottrich.github.io.smartchecklist.presentation.ui.checklistsettings

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import wottrich.github.io.smartchecklist.R
import wottrich.github.io.smartchecklist.baseui.R as BaseUiR
import wottrich.github.io.smartchecklist.presentation.viewmodel.ChecklistSettingUiEffect.CloseScreen
import wottrich.github.io.smartchecklist.presentation.viewmodel.ChecklistSettingsAllTasksAction.CHECK_ALL
import wottrich.github.io.smartchecklist.presentation.viewmodel.ChecklistSettingsAllTasksAction.UNCHECK_ALL
import wottrich.github.io.smartchecklist.presentation.viewmodel.ChecklistSettingsViewModel
import wottrich.github.io.smartchecklist.baseui.TopBarContent
import wottrich.github.io.smartchecklist.baseui.components.SmartChecklistButton
import wottrich.github.io.smartchecklist.baseui.icons.ArrowBackIcon
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.baseui.ui.pallet.SmartChecklistTheme


@Composable
fun ChecklistSettingsScreen(
    onCloseScreen: () -> Unit
) {
    ApplicationTheme {
        ScreenAndEffects(onCloseScreen)
    }
}

@Composable
private fun ScreenAndEffects(
    onCloseScreen: () -> Unit,
    viewModel: ChecklistSettingsViewModel = getViewModel()
) {
    Effects(viewModel, onCloseScreen)
    Screen(onCloseScreen, viewModel)
}

@OptIn(InternalCoroutinesApi::class)
@Composable
private fun Effects(
    viewModel: ChecklistSettingsViewModel,
    onCloseScreen: () -> Unit
) {
    val effects = viewModel.uiEffect
    LaunchedEffect(key1 = effects) {
        effects.collect(
            FlowCollector {
                when (it) {
                    CloseScreen -> onCloseScreen()
                }
            }
        )
    }
}

@Composable
private fun Screen(
    onBackButton: () -> Unit,
    viewModel: ChecklistSettingsViewModel
) {
    Scaffold(
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
        val state by viewModel.uiState.collectAsState()
        Column(
            modifier = Modifier.padding(it)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = Dimens.BaseFour.SizeFour)
                    .padding(top = Dimens.BaseFour.SizeTwo)
            ) {
                Text(text = stringResource(id = R.string.checklist_settings_screen_task_action_label))
                Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeTwo))
                Switcher(
                    state = state.allTasksAction,
                    condition = { if (state.allTasksAction == CHECK_ALL) -1f else 1f },
                    firstOption = {
                        SwitcherOptionsContent(label = stringResource(id = R.string.checklist_settings_screen_check_all_label)) {
                            viewModel.onChangeSwitcher(CHECK_ALL)
                        }
                    },
                    secondOption = {
                        SwitcherOptionsContent(label = stringResource(id = R.string.checklist_settings_screen_uncheck_all_label)) {
                            viewModel.onChangeSwitcher(UNCHECK_ALL)
                        }
                    }
                )
            }

            val confirmStringResource = stringResource(id = BaseUiR.string.confirm)
            SmartChecklistButton(
                modifier = Modifier.fillMaxWidth(),
                onClick = viewModel::onConfirmClicked,
                buttonContentDescription = confirmStringResource
            ) {
                Text(text = confirmStringResource)
            }
        }
    }
}

@Composable
private fun <T> Switcher(
    state: T?,
    condition: (T) -> Float,
    firstOption: @Composable() RowScope.() -> Unit,
    secondOption: @Composable() RowScope.() -> Unit,
) {
    val transition = updateTransition(state, "UpdateTransition")
    val animatedFloat = transition.animateFloat(
        transitionSpec = { tween(500) },
        label = "UpdateTransitionAnimation"
    ) {
        if (it == null) -1f else condition(it)
    }
    val align = BiasAlignment(animatedFloat.value, 0f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.BaseFour.SizeTwo))
            .background(SmartChecklistTheme.colors.surface)
            .height(Dimens.BaseFour.SizeTen)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.5f)
                .align(align)
                .clip(RoundedCornerShape(Dimens.BaseFour.SizeTwo))
                .background(
                    SmartChecklistTheme.colors.secondaryVariant.copy(
                        alpha = if (state != null) ContentAlpha.high else ContentAlpha.disabled
                    )
                )
        )
        Row(modifier = Modifier.fillMaxHeight()) {
            firstOption()
            secondOption()
        }
    }
}

@Composable
private fun RowScope.SwitcherOptionsContent(label: String, onClick: () -> Unit) {
    Text(
        modifier = Modifier
            .fillMaxHeight()
            .weight(1f)
            .clip(RoundedCornerShape(Dimens.BaseFour.SizeTwo))
            .clickable { onClick() }
            .padding(vertical = Dimens.BaseFour.SizeTwo)
            .padding(end = Dimens.BaseFour.SizeTwo),
        text = label,
        color = SmartChecklistTheme.colors.onSurface,
        textAlign = TextAlign.Center
    )
}
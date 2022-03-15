package wottrich.github.io.androidsmartchecklist.presentation.ui.checklistsettings

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import github.io.wottrich.impl.R.string
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.ChecklistSettingsAllTasksAction
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.ChecklistSettingsAllTasksAction.CHECK_ALL
import wottrich.github.io.androidsmartchecklist.presentation.viewmodel.ChecklistSettingsAllTasksAction.UNCHECK_ALL
import wottrich.github.io.baseui.TopBarContent
import wottrich.github.io.baseui.ui.ApplicationTheme
import wottrich.github.io.baseui.ui.Dimens
import wottrich.github.io.baseui.ui.Dimens.BaseFour
import wottrich.github.io.baseui.ui.pallet.SmartChecklistTheme


@Composable
fun ChecklistSettingsScreen(
    onBackButton: () -> Unit
) {
    ApplicationTheme {
        Screen(onBackButton)
    }
}

@Composable
private fun Screen(onBackButton: () -> Unit) {
    Scaffold(
        topBar = {
            TopBarContent(
                title = {
                    Text("Checklist Settings Screen")
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = string.arrow_back_content_description),
                        tint = MaterialTheme.colors.onPrimary
                    )
                },
                navigationIconAction = onBackButton
            )
        }
    ) {
        Column {
            var isCheckAllTasksChecked by remember {
                mutableStateOf(CHECK_ALL)
            }
            Switcher(
                state = isCheckAllTasksChecked,
                condition = { if (isCheckAllTasksChecked == CHECK_ALL) -1f else 1f },
                firstOption = {
                    SwitcherOptionsContent(label = "Check All") {
                        isCheckAllTasksChecked = CHECK_ALL
                    }
                },
                secondOption = {
                    SwitcherOptionsContent(label = "Uncheck All") {
                        isCheckAllTasksChecked = UNCHECK_ALL
                    }
                }
            )
        }
    }
}

@Composable
private fun <T> Switcher(
    state: T,
    condition: (T) -> Float,
    firstOption: @Composable() RowScope.() -> Unit,
    secondOption: @Composable() RowScope.() -> Unit,
) {
    val transition = updateTransition(state, "UpdateTransition")
    val animatedFloat = transition.animateFloat(
        transitionSpec = { tween(500) },
        label = "UpdateTransitionAnimation"
    ) {
        condition(it)
    }
    val align = BiasAlignment(animatedFloat.value, 0f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = BaseFour.SizeFour)
            .clip(RoundedCornerShape(BaseFour.SizeTwo))
            .background(SmartChecklistTheme.colors.surface)
            .height(BaseFour.SizeTen)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.5f)
                .align(align)
                .clip(RoundedCornerShape(BaseFour.SizeTwo))
                .background(SmartChecklistTheme.colors.secondaryVariant)
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
            .clip(RoundedCornerShape(BaseFour.SizeTwo))
            .clickable { onClick() }
            .padding(vertical = BaseFour.SizeTwo)
            .padding(end = BaseFour.SizeTwo),
        text = label,
        color = SmartChecklistTheme.colors.onSurface,
        textAlign = TextAlign.Center
    )
}
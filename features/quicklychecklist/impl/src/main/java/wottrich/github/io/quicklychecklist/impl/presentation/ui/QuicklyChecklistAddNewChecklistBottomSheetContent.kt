package wottrich.github.io.quicklychecklist.impl.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens.BaseFour
import wottrich.github.io.smartchecklist.baseui.ui.color.defaultButtonColors
import wottrich.github.io.smartchecklist.baseui.ui.color.defaultOutlinedTextFieldColors
import wottrich.github.io.androidsmartchecklist.quicklychecklist.R
import wottrich.github.io.quicklychecklist.impl.presentation.states.QuicklyChecklistAddNewChecklistUiEffect.OnAddNewChecklistCompleted
import wottrich.github.io.quicklychecklist.impl.presentation.states.QuicklyChecklistAddNewChecklistUiEffect.OnAddNewChecklistFailure
import wottrich.github.io.quicklychecklist.impl.presentation.viewmodels.QuicklyChecklistAddNewChecklistViewModel

@Composable
fun QuicklyChecklistAddNewChecklistBottomSheetContent(
    quicklyChecklistJson: String,
    onConfirmButtonClick: () -> Unit,
    viewModel: QuicklyChecklistAddNewChecklistViewModel = getViewModel {
        parametersOf(quicklyChecklistJson)
    }
) {
    ApplicationTheme {
        Effect(viewModel = viewModel, onConfirmButtonClick = onConfirmButtonClick)
        Screen(viewModel = viewModel)
    }
}

@Composable
private fun Effect(
    viewModel: QuicklyChecklistAddNewChecklistViewModel,
    onConfirmButtonClick: () -> Unit
) {
    val effects = viewModel.effects
    val context = LocalContext.current
    LaunchedEffect(key1 = effects) {
        effects.collect { effect ->
            when (effect) {
                OnAddNewChecklistCompleted -> {
                    Toast.makeText(context, context.getString(R.string.quickly_checklist_add_new_checklist_completed), Toast.LENGTH_SHORT).show()
                    onConfirmButtonClick()
                }
                OnAddNewChecklistFailure -> {
                    Toast.makeText(context, context.getString(R.string.quickly_checklist_add_new_checklist_failure), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@Composable
private fun Screen(viewModel: QuicklyChecklistAddNewChecklistViewModel) {
    val state by viewModel.uiState.collectAsState()
    Column {
        TextField(
            value = state.checklistName,
            onValueChange = viewModel::onTextChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = stringResource(id = R.string.quickly_checklist_type_checklist_name_hint))
            },
            colors = defaultOutlinedTextFieldColors()
        )
        Spacer(modifier = Modifier.height(BaseFour.SizeTwo))
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = state.isButtonEnabled,
            onClick = viewModel::onConfirmButtonClicked,
            colors = defaultButtonColors()
        ) {
            Text(text = stringResource(id = R.string.quickly_checklist_confirm_new_checklist_name))
        }
    }
}
package github.io.wottrich.checklist.presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import github.io.wottrich.checklist.presentation.viewmodel.ChecklistNameUiEffects
import github.io.wottrich.checklist.presentation.viewmodel.ChecklistNameUiEffects.InvalidChecklistState
import github.io.wottrich.checklist.presentation.viewmodel.ChecklistNameViewModel
import github.io.wottrich.impl.R
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.collect
import org.koin.androidx.compose.getViewModel
import wottrich.github.io.baseui.TextOneLine
import wottrich.github.io.baseui.ui.Dimens
import wottrich.github.io.baseui.ui.color.defaultButtonColors
import wottrich.github.io.baseui.ui.color.defaultOutlinedTextFieldColors

@InternalCoroutinesApi
@Composable
fun ChecklistNameScreen(
    scaffoldState: ScaffoldState,
    viewModel: ChecklistNameViewModel = getViewModel(),
    onNext: (checklistId: String) -> Unit
) {

    val state by viewModel.state.collectAsState()
    val effects = viewModel.effects

    val stringError = stringResource(id = R.string.unknown)
    LaunchedEffect(key1 = effects) {
        effects.collect { effect ->
            when (effect) {
                is ChecklistNameUiEffects.NextScreen -> onNext(effect.checklistId)
                InvalidChecklistState -> {
                    scaffoldState.snackbarHostState.showSnackbar(stringError)
                }
            }
        }
    }

    Screen(
        textFieldValue = state.checklistName,
        isConfirmButtonEnabled = state.isConfirmButtonEnabled,
        onTextFieldValueChange = viewModel::onTextChange,
        onConfirmButtonClicked = viewModel::onConfirmButtonClicked
    )
}

@Composable
private fun Screen(
    textFieldValue: String,
    isConfirmButtonEnabled: Boolean,
    onTextFieldValueChange: (String) -> Unit,
    onConfirmButtonClicked: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(all = Dimens.BaseFour.SizeThree)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            TextOneLine(
                primary = {
                    Text(text = stringResource(id = R.string.checklist_name_screen_type_checklist_name_hint))
                }
            )

            Spacer(modifier = Modifier.height(Dimens.BaseFour.SizeTwo))

            OutlinedTextField(
                value = textFieldValue,
                onValueChange = onTextFieldValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = stringResource(id = R.string.checklist_name_screen_example_hint))
                },
                colors = defaultOutlinedTextFieldColors()
            )
        }
        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = isConfirmButtonEnabled,
            onClick = onConfirmButtonClicked,
            colors = defaultButtonColors()
        ) {
            Text(text = stringResource(id = R.string.checklist_name_screen_button_continue))
        }
    }
}
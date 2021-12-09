package github.io.wottrich.checklist.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import github.io.wottrich.checklist.presentation.viewmodel.ChecklistNameScreenState
import github.io.wottrich.checklist.presentation.viewmodel.ChecklistNameViewModel
import github.io.wottrich.impl.R
import kotlinx.coroutines.InternalCoroutinesApi
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

    var textFieldValue by rememberSaveable { mutableStateOf("") }
    val state by viewModel.state.collectAsState(initial = ChecklistNameScreenState.InitialState)

    when {
        state.hasError && state is ChecklistNameScreenState.InvalidChecklistState -> {
            val stringError = stringResource(id = R.string.unknown)
            LaunchedEffect(scaffoldState.snackbarHostState) {
                scaffoldState.snackbarHostState.showSnackbar(stringError).let {
                    if (it == SnackbarResult.Dismissed) {
                        viewModel.updateState(ChecklistNameScreenState.InitialState)
                    }
                }
            }
        }
        state is ChecklistNameScreenState.NextScreen -> {
            onNext((state as ChecklistNameScreenState.NextScreen).checklistId)
        }
    }

    Screen(
        textFieldValue = textFieldValue,
        onTextFieldValueChange = { textFieldValue = it },
        onConfirm = {
            viewModel.nextScreen(textFieldValue)
        }
    )
}

@Composable
private fun Screen(
    textFieldValue: String,
    onTextFieldValueChange: (String) -> Unit,
    onConfirm: () -> Unit
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
            enabled = textFieldValue.isNotEmpty(),
            onClick = onConfirm,
            colors = defaultButtonColors()
        ) {
            Text(text = stringResource(id = R.string.checklist_name_screen_button_continue))
        }
    }
}
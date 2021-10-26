package wottrich.github.io.featurenew.view.screens.checklistname

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.ScaffoldState
import androidx.compose.material.SnackbarResult
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.onEach
import org.koin.androidx.compose.getViewModel
import wottrich.github.io.components.TitleRow
import wottrich.github.io.components.ui.Sizes
import wottrich.github.io.components.ui.defaultButtonColors
import wottrich.github.io.components.ui.defaultOutlinedTextFieldColors
import wottrich.github.io.featurenew.R
import wottrich.github.io.tools.observeInLifecycle

@InternalCoroutinesApi
@Composable
fun ChecklistNameScreen(
    scaffoldState: ScaffoldState,
    viewModel: ChecklistNameViewModel = getViewModel(),
    onNext: (checklistId: String) -> Unit
) {

    var textFieldValue by rememberSaveable { mutableStateOf("") }
    viewModel.nextScreenEvent.onEach {
        onNext(it)
    }.observeInLifecycle(LocalLifecycleOwner.current)
    
    val state by viewModel.state.collectAsState(initial = ChecklistNameScreenState.InitialState)

    when {
        state.hasError -> {
            val stateError = state as ChecklistNameScreenState.ErrorState
            LaunchedEffect(scaffoldState.snackbarHostState) {
                scaffoldState.snackbarHostState.showSnackbar(
                    stateError.exception?.message.orEmpty()
                ).let {
                    if (it == SnackbarResult.Dismissed) {
                        viewModel.updateState(ChecklistNameScreenState.InitialState)
                    }
                }
            }
        }
    }

    Screen(
        textFieldValue = textFieldValue,
        onTextFieldValueChange = { textFieldValue = it },
        onConfirm = { viewModel.nextScreen(textFieldValue) }
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
            .padding(all = Sizes.x12)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            TitleRow(text = stringResource(id = R.string.new_checklist_type_checklist_name_hint))

            OutlinedTextField(
                value = textFieldValue,
                onValueChange = onTextFieldValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = stringResource(id = R.string.new_checklist_example_hint))
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
            Text(text = stringResource(id = R.string.new_checklist_button_continue))
        }
    }
}
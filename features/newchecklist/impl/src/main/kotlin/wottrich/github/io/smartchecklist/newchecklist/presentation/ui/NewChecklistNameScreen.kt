package wottrich.github.io.smartchecklist.newchecklist.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import org.koin.androidx.compose.getViewModel
import wottrich.github.io.smartchecklist.baseui.components.SmartChecklistButton
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens.BaseFour
import wottrich.github.io.smartchecklist.baseui.ui.color.defaultOutlinedTextFieldColors
import wottrich.github.io.smartchecklist.newchecklist.R
import wottrich.github.io.smartchecklist.newchecklist.presentation.states.NewChecklistNameUiEffect
import wottrich.github.io.smartchecklist.newchecklist.presentation.states.NewChecklistNameUiState
import wottrich.github.io.smartchecklist.newchecklist.presentation.viewmodels.NewChecklistNameViewModel
import wottrich.github.io.smartchecklist.baseui.R as BaseUiR

@Composable
fun NewChecklistNameScreen(
    onCloseScreen: () -> Unit,
    viewModel: NewChecklistNameViewModel = getViewModel()
) {
    val scaffoldState = rememberScaffoldState()
    ApplicationTheme {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                IconButton(onClick = onCloseScreen) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = BaseUiR.string.arrow_back_content_description),
                        tint = MaterialTheme.colors.onPrimary
                    )
                }
            }
        ) {
            val state by viewModel.state.collectAsState()
            Effects(
                viewModel = viewModel,
                snackbarHostState = scaffoldState.snackbarHostState,
                onCloseScreen = onCloseScreen
            )
            Screen(
                state = state,
                contentPaddingValues = it,
                onTextFieldValueChange = { text ->
                    viewModel.onTextChange(text)
                },
                onDoneButtonClicked = {
                    viewModel.onDoneButtonClicked()
                }
            )
        }
    }
}

@Composable
private fun Screen(
    state: NewChecklistNameUiState,
    contentPaddingValues: PaddingValues,
    onTextFieldValueChange: (String) -> Unit,
    onDoneButtonClicked: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .padding(contentPaddingValues)
            .padding(all = BaseFour.SizeThree)
    ) {
        Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                value = state.checklistName,
                onValueChange = onTextFieldValueChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(text = stringResource(id = R.string.checklist_name_screen_type_checklist_name_hint))
                },
                colors = defaultOutlinedTextFieldColors(),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { onDoneButtonClicked() }
                )
            )
        }
        SmartChecklistButton(
            onClick = onDoneButtonClicked,
            modifier = Modifier.fillMaxWidth(),
            enabled = state.isDoneButtonEnabled,
        ) {
            Text(text = stringResource(id = R.string.checklist_name_create_checklist_button))
        }
    }
}

@Composable
private fun Effects(
    viewModel: NewChecklistNameViewModel,
    snackbarHostState: SnackbarHostState,
    onCloseScreen: () -> Unit,
) {
    val effects = viewModel.effects
    val createChecklistFailedMessage = stringResource(id = R.string.checklist_name_create_checklist_failed_message)
    LaunchedEffect(key1 = effects) {
        effects.collect { effect ->
            when (effect) {
                is NewChecklistNameUiEffect.CloseScreen -> onCloseScreen()
                NewChecklistNameUiEffect.CreateChecklistFailed -> {
                    snackbarHostState.showSnackbar(createChecklistFailedMessage)
                }
            }
        }
    }
}
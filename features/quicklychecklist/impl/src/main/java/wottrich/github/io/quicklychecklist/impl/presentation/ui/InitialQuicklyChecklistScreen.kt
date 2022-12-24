package wottrich.github.io.quicklychecklist.impl.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf
import wottrich.github.io.baseui.StyledText
import wottrich.github.io.baseui.ui.ApplicationTheme
import wottrich.github.io.baseui.ui.Dimens.BaseFour
import wottrich.github.io.baseui.ui.color.defaultButtonColors
import wottrich.github.io.quicklychecklist.impl.R
import wottrich.github.io.quicklychecklist.impl.R.string
import wottrich.github.io.quicklychecklist.impl.presentation.states.InitialQuicklyChecklistUiEffect.OnInvalidChecklist
import wottrich.github.io.quicklychecklist.impl.presentation.states.InitialQuicklyChecklistUiEffect.OnQuicklyChecklistJson
import wottrich.github.io.quicklychecklist.impl.presentation.viewmodels.InitialQuicklyChecklistViewModel

@Composable
fun InitialQuicklyChecklistScreen(
    encodedQuicklyChecklist: String?,
    isInvalidChecklistError: Boolean,
    onBackPressed: () -> Unit,
    onConfirmButtonClicked: (String) -> Unit
) {
    ApplicationTheme {
        Screen(
            encodedQuicklyChecklist = encodedQuicklyChecklist,
            isInvalidChecklistError = isInvalidChecklistError,
            onBackPressed = onBackPressed,
            onConfirmButtonClicked = onConfirmButtonClicked
        )
    }
}

@Composable
private fun Screen(
    encodedQuicklyChecklist: String?,
    isInvalidChecklistError: Boolean,
    onBackPressed: () -> Unit,
    onConfirmButtonClicked: (String) -> Unit,
    viewModel: InitialQuicklyChecklistViewModel = getViewModel {
        parametersOf(encodedQuicklyChecklist)
    }
) {
    val rememberScaffoldState = rememberScaffoldState()

    ScreenEffect(
        scaffoldState = rememberScaffoldState,
        isInvalidChecklistError = isInvalidChecklistError,
        viewModel = viewModel,
        onConfirmButtonClicked = onConfirmButtonClicked
    )

    ScreenContent(
        scaffoldState = rememberScaffoldState,
        onBackPressed = onBackPressed,
        viewModel = viewModel
    )
}

@Composable
private fun ScreenEffect(
    scaffoldState: ScaffoldState,
    isInvalidChecklistError: Boolean,
    onConfirmButtonClicked: (String) -> Unit,
    viewModel: InitialQuicklyChecklistViewModel
) {
    val effects = viewModel.effects
    LaunchedEffect(key1 = effects) {
        effects.collect { effect ->
            when (effect) {
                is OnQuicklyChecklistJson -> onConfirmButtonClicked(effect.quicklyChecklistJson)
                is OnInvalidChecklist -> {
                    scaffoldState.snackbarHostState.showSnackbar("invalid checklist")
                }
            }
        }
    }
    if (isInvalidChecklistError) {
        viewModel.onInvalidChecklist()
    }
}

@Composable
private fun ScreenContent(
    scaffoldState: ScaffoldState,
    onBackPressed: () -> Unit,
    viewModel: InitialQuicklyChecklistViewModel
) {
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.arrow_back_content_description),
                    tint = MaterialTheme.colors.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(paddingValues)
                .padding(all = BaseFour.SizeThree)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (viewModel.isChecklistReceived) {
                    StyledText(
                        textStyle = MaterialTheme.typography.h6
                    ) {
                        Text(
                            text = stringResource(id = string.quickly_checklist_initial_screen_success_title),
                            textAlign = TextAlign.Center,
                        )
                    }
                } else {
                    StyledText(
                        textStyle = MaterialTheme.typography.h6
                    ) {
                        Text(
                            text = stringResource(id = string.quickly_checklist_initial_screen_processing_title),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                enabled = viewModel.isButtonEnabled,
                onClick = viewModel::onConfirmButtonClicked,
                colors = defaultButtonColors()
            ) {
                Text(text = stringResource(id = R.string.default_continue))
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun QuicklyChecklistScreenPreview() {
    ApplicationTheme {
        InitialQuicklyChecklistScreen(
            encodedQuicklyChecklist = null,
            isInvalidChecklistError = false,
            onBackPressed = {},
            onConfirmButtonClicked = {}
        )
    }
}
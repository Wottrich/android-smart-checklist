package wottrich.github.io.smartchecklist.suggestion.presentation.ui.tagregister

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.getViewModel
import wottrich.github.io.smartchecklist.baseui.TopBarContent
import wottrich.github.io.smartchecklist.baseui.components.ArrowBackIconComponent
import wottrich.github.io.smartchecklist.baseui.components.SmartChecklistButton
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.suggestion.R

@Composable
fun RegisterNewTagWithSuggestionsScreen(
    onBackPressed: () -> Unit,
    viewModel: RegisterNewTagWithSuggestionsViewModel = getViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ApplicationTheme {
        Screen(
            uiState,
            viewModel,
            onBackPressed = onBackPressed
        )
    }
}

@Composable
private fun Screen(
    uiModel: RegisterNewTagWithSuggestionsUiModel,
    action: RegisterNewTagWithSuggestionsViewModelAction,
    onBackPressed: () -> Unit
) {
    Scaffold(
        topBar = {
            TopBarContent(
                navigationIcon = {
                    ArrowBackIconComponent()
                },
                navigationIconAction = { onBackPressed() }
            ) {
                Text(text = stringResource(id = R.string.tags_register_screen_toolbar_title))
            }
        },
        bottomBar = {
            AnimatedVisibility(
                visible = uiModel.shouldShowRegisterNewTagButton,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colors.background),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    SmartChecklistButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "Registrar nova tag")
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = Dimens.BaseFour.SizeThree)
        ) {
            HeaderComponentContent(uiModel, action)
            SuggestionLazyColumnContent(suggestions = uiModel.suggestions)
        }
    }
}

@Composable
private fun HeaderComponentContent(
    uiModel: RegisterNewTagWithSuggestionsUiModel,
    action: RegisterNewTagWithSuggestionsViewModelAction
) {
    Column(
        modifier = Modifier
            .shadow(elevation = 1.dp)
            .padding(horizontal = Dimens.BaseFour.SizeThree)
            .padding(top = Dimens.BaseFour.SizeThree)
    ) {
        EditTagNameComponent(
            tagName = uiModel.tagName,
            onTextFieldValueChange = { text ->
                action.sendAction(
                    RegisterNewTagWithSuggestionsViewModelAction.Action.OnTagNameTextChange(
                        text
                    )
                )
            }
        )
        RegisterSuggestionOutputTextFieldComponent(
            suggestionName = uiModel.suggestionName,
            onTextFieldValueChange = { text ->
                action.sendAction(
                    RegisterNewTagWithSuggestionsViewModelAction.Action.OnSuggestionNameTextChange(
                        text
                    )
                )
            },
            onAddButtonClicked = {
                action.sendAction(RegisterNewTagWithSuggestionsViewModelAction.Action.OnAddSuggestionClicked)
            }
        )
    }
}

@Preview
@Composable
internal fun RegisterNewTagWithSuggestionsScreenPreview() {
    ApplicationTheme {
        Screen(
            uiModel = RegisterNewTagWithSuggestionsUiModel(
                isEditMode = false,
                shouldShowRegisterNewTagButton = true,
                tagName = "tag name",
                suggestionName = "",
                suggestions = listOf()
            ),
            action = {},
            {}
        )
    }
}
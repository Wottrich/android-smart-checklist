package wottrich.github.io.smartchecklist.suggestion.presentation.ui.tagsoverview

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import org.koin.androidx.compose.getViewModel
import wottrich.github.io.smartchecklist.baseui.TopBarContent
import wottrich.github.io.smartchecklist.baseui.previewparameters.BooleanPreviewParameter
import wottrich.github.io.smartchecklist.baseui.ui.ApplicationTheme
import wottrich.github.io.smartchecklist.baseui.ui.RowDefaults
import wottrich.github.io.smartchecklist.baseui.ui.TextStateComponent
import wottrich.github.io.smartchecklist.datasource.entity.SuggestionEntity
import wottrich.github.io.smartchecklist.datasource.entity.TagEntity
import wottrich.github.io.smartchecklist.datasource.entity.TagSuggestionEmbedded
import wottrich.github.io.smartchecklist.suggestion.R
import wottrich.github.io.smartchecklist.suggestion.presentation.state.TagScreenUiModel
import wottrich.github.io.smartchecklist.suggestion.presentation.state.TagsOverviewUiEffect
import wottrich.github.io.smartchecklist.suggestion.presentation.state.TagsOverviewUiState
import wottrich.github.io.smartchecklist.suggestion.presentation.state.TagsOverviewViewModelActions
import wottrich.github.io.smartchecklist.suggestion.presentation.viewmodel.TagsOverviewViewModel
import wottrich.github.io.smartchecklist.baseui.R as BaseUiR

sealed class TagsScreenNavigationAction {
    object CloseScreen : TagsScreenNavigationAction()
    data class OpenTagDetail(val tagUuid: String) : TagsScreenNavigationAction()
    object AddNewTag : TagsScreenNavigationAction()
}

@Composable
fun TagsOverviewScreen(onNavigationAction: (TagsScreenNavigationAction) -> Unit) {
    ApplicationTheme {
        ScreenComponents(onNavigationAction = onNavigationAction)
    }
}

@Composable
private fun ScreenComponents(
    onNavigationAction: (TagsScreenNavigationAction) -> Unit,
    viewModel: TagsOverviewViewModel = getViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    ScreenEffects(viewModel = viewModel, onNavigationAction = onNavigationAction)
    ScreenScaffold(
        state = state,
        onAction = viewModel::sendAction
    )
}

@Composable
private fun ScreenEffects(
    viewModel: TagsOverviewViewModel,
    onNavigationAction: (TagsScreenNavigationAction) -> Unit
) {
    val effects = viewModel.uiEffect
    LaunchedEffect(key1 = effects) {
        effects.collect { effect ->
            when (effect) {
                TagsOverviewUiEffect.CloseScreen -> onNavigationAction(TagsScreenNavigationAction.CloseScreen)
                is TagsOverviewUiEffect.OpenTagDetail -> onNavigationAction(
                    TagsScreenNavigationAction.OpenTagDetail(effect.tagUuid)
                )

                TagsOverviewUiEffect.AddNewTag -> onNavigationAction(TagsScreenNavigationAction.AddNewTag)
            }
        }
    }
}

@Composable
private fun ScreenScaffold(
    state: TagsOverviewUiState,
    onAction: (TagsOverviewViewModelActions.Action) -> Unit,
) {
    Scaffold(
        topBar = {
            TopBarContent(
                title = {
                    TextStateComponent(
                        textState = RowDefaults.title(text = stringResource(id = R.string.tags_overview_toolbar_title))
                    )
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = BaseUiR.string.arrow_back_content_description),
                        tint = MaterialTheme.colors.onPrimary
                    )
                },
                navigationIconAction = { onAction(TagsOverviewViewModelActions.Action.OnCloseScreen) }
            )        },
//        floatingActionButton = {
//            if (tagScreenUiState !is TagScreenUiState.Error) {
//                FloatingActionButton(onClick = { /*TODO*/ }) {
//
//                }
//            }
//        }

    ) {
        Box(modifier = Modifier.padding(it)) {
            Screen(
                state = state,
                onAction = onAction
            )
        }
    }
}

@Composable
private fun Screen(
    state: TagsOverviewUiState,
    onAction: (TagsOverviewViewModelActions.Action) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Crossfade(targetState = state, label = "TagsOverviewStateCrossfade") {
            when (it) {
                is TagsOverviewUiState.Error -> TagsOverviewErrorStateContent {
                    onAction(TagsOverviewViewModelActions.Action.OnTryAgain)
                }
                TagsOverviewUiState.Loading -> TagsOverviewLoadingStateContent()
                is TagsOverviewUiState.EmptyList -> TagOverviewEmptyStateContent {
                    onAction(TagsOverviewViewModelActions.Action.OnRegisterNewTag)
                }
                is TagsOverviewUiState.Resume -> TagsOverviewResumeStateContent(it, onAction)
            }
        }
    }
}

@Preview
@Composable
fun TagsOverviewResumePreview(
    @PreviewParameter(BooleanPreviewParameter::class) isDarkMode: Boolean
) {
    ApplicationTheme(isDarkMode) {
        ScreenScaffold(
            state = TagsOverviewUiState.Resume(
                TagScreenUiModel(
                    listOf(
                        TagSuggestionEmbedded(
                            TagEntity(name = "Mercado"),
                            listOf()
                        ),
                        TagSuggestionEmbedded(
                            TagEntity(name = "Feira"),
                            listOf(
                                SuggestionEntity(
                                    name = "Tomate",
                                    parentUuid = "123"
                                )
                            )
                        )
                    )
                )
            ),
            onAction = {}
        )
    }
}

@Preview
@Composable
fun TagsOverviewLoadingPreview(
    @PreviewParameter(BooleanPreviewParameter::class) isDarkMode: Boolean
) {
    ApplicationTheme(isDarkMode) {
        ScreenScaffold(
            state = TagsOverviewUiState.Loading,
            onAction = {}
        )
    }
}

@Preview
@Composable
fun TagsOverviewEmptyPreview(
    @PreviewParameter(BooleanPreviewParameter::class) isDarkMode: Boolean
) {
    ApplicationTheme(isDarkMode) {
        ScreenScaffold(
            state = TagsOverviewUiState.EmptyList(TagScreenUiModel(listOf())),
            onAction = {}
        )
    }
}
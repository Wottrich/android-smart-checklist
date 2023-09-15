package wottrich.github.io.smartchecklist.suggestion.presentation.ui.tagsoverview

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import wottrich.github.io.smartchecklist.baseui.ui.Dimens
import wottrich.github.io.smartchecklist.suggestion.presentation.state.TagsOverviewUiState
import wottrich.github.io.smartchecklist.suggestion.presentation.state.TagsOverviewViewModelActions
import wottrich.github.io.smartchecklist.suggestion.presentation.ui.TagMoleculeState
import wottrich.github.io.smartchecklist.suggestion.presentation.ui.TagsListingComponent

@Composable
internal fun TagsOverviewResumeStateContent(
    state: TagsOverviewUiState.Resume,
    onAction: (TagsOverviewViewModelActions.Action) -> Unit
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = Dimens.BaseFour.SizeTwo)) {
        TagsListingComponent(
            tags = state.uiModel.tags.map { TagMoleculeState.SettingTags(it) },
            onTagDetailClick = { onAction(TagsOverviewViewModelActions.Action.OnTagDetailClicked(it)) }
        )
    }
}
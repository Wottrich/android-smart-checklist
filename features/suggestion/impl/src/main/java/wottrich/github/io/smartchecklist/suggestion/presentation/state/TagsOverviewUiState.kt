package wottrich.github.io.smartchecklist.suggestion.presentation.state

import androidx.annotation.StringRes

sealed class TagsOverviewUiState(open val uiModel: TagScreenUiModel) {
    object Loading : TagsOverviewUiState(TagScreenUiModel())
    data class Resume(override val uiModel: TagScreenUiModel) : TagsOverviewUiState(uiModel)
    data class EmptyList(override val uiModel: TagScreenUiModel) : TagsOverviewUiState(uiModel)
    data class Error(
        override val uiModel: TagScreenUiModel,
        @StringRes val errorMessage: Int
    ) : TagsOverviewUiState(uiModel)
}
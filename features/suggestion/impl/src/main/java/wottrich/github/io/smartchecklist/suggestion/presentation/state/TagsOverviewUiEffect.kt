package wottrich.github.io.smartchecklist.suggestion.presentation.state

sealed class TagsOverviewUiEffect {
    object CloseScreen : TagsOverviewUiEffect()
    data class OpenTagDetail(val tagUuid: String) : TagsOverviewUiEffect()
    object AddNewTag : TagsOverviewUiEffect()
}
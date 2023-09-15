package wottrich.github.io.smartchecklist.suggestion.presentation.state

import wottrich.github.io.smartchecklist.suggestion.TagSuggestionEmbeddedContract

data class TagScreenUiModel(
    val tags: List<TagSuggestionEmbeddedContract> = listOf()
)
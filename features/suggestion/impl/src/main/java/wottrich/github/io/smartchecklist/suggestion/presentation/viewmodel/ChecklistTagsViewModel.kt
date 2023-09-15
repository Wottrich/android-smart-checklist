package wottrich.github.io.smartchecklist.suggestion.presentation.viewmodel

import wottrich.github.io.smartchecklist.android.BaseViewModel
import wottrich.github.io.smartchecklist.coroutines.base.onFailure
import wottrich.github.io.smartchecklist.coroutines.base.onSuccess
import wottrich.github.io.smartchecklist.datasource.entity.ChecklistTagsEmbeddedContract
import wottrich.github.io.smartchecklist.suggestion.TagContract
import wottrich.github.io.smartchecklist.suggestion.TagSuggestionEmbeddedContract
import wottrich.github.io.smartchecklist.suggestion.domain.usecase.GetAllTagsUseCase
import wottrich.github.io.smartchecklist.suggestion.domain.usecase.GetChecklistTagsUseCase
import wottrich.github.io.smartchecklist.suggestion.presentation.ui.TagMoleculeState

class ChecklistTagsViewModel(
    private val checklistUuid: String,
    private val getChecklistTagsUseCase: GetChecklistTagsUseCase,
    private val getAllTagsUseCase: GetAllTagsUseCase
) : BaseViewModel() {

    private val tags = mutableListOf<TagMoleculeState>()

    init {
        launchIO {
            getChecklistTagsUseCase(checklistUuid).onSuccess {
                getAllTags(it)
            }.onFailure {
                // TODO add failure snackbar
            }
        }
    }

    private fun getAllTags(checklistTagsEmbedded: ChecklistTagsEmbeddedContract) {
        launchIO {
            getAllTagsUseCase().onSuccess {
                mapTagsEmbeddedWithChecklist(checklistTagsEmbedded.tags, it)
            }.onFailure {
                // TODO add failure snackbar
            }
        }
    }

    private fun mapTagsEmbeddedWithChecklist(
        checklistTags: List<TagContract>,
        allTags: List<TagSuggestionEmbeddedContract>
    ) {
        val mappedTags = tagWithSelectedChecklistTags(allTags, checklistTags)
        tags.clear()
        tags.addAll(mappedTags)
    }

    private fun tagWithSelectedChecklistTags(
        allTags: List<TagSuggestionEmbeddedContract>,
        checklistTags: List<TagContract>
    ) = allTags.map {
        TagMoleculeState.SelectableTags(
            isSelected = checklistTags.contains(it.tag),
            tagSuggestionEmbeddedContract = it
        )
    }

}
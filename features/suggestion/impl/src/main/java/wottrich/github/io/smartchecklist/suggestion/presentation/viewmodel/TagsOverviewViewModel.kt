package wottrich.github.io.smartchecklist.suggestion.presentation.viewmodel

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import wottrich.github.io.smartchecklist.android.BaseViewModel
import wottrich.github.io.smartchecklist.coroutines.base.onFailure
import wottrich.github.io.smartchecklist.coroutines.base.onSuccess
import wottrich.github.io.smartchecklist.kotlin.SingleShotEventBus
import wottrich.github.io.smartchecklist.suggestion.R
import wottrich.github.io.smartchecklist.suggestion.domain.usecase.GetAllTagsUseCase
import wottrich.github.io.smartchecklist.suggestion.presentation.state.TagsOverviewUiEffect
import wottrich.github.io.smartchecklist.suggestion.presentation.state.TagsOverviewUiState
import wottrich.github.io.smartchecklist.suggestion.presentation.state.TagsOverviewViewModelActions

class TagsOverviewViewModel(
    private val getAllTagsUseCase: GetAllTagsUseCase
) : BaseViewModel(), TagsOverviewViewModelActions {

    private val _uiState: MutableStateFlow<TagsOverviewUiState> =
        MutableStateFlow(TagsOverviewUiState.Loading)
    val uiState = _uiState.asStateFlow()

    private val _uiEffect = SingleShotEventBus<TagsOverviewUiEffect>()
    val uiEffect: Flow<TagsOverviewUiEffect> = _uiEffect.events

    init {
        fetchTags()
    }

    override fun sendAction(action: TagsOverviewViewModelActions.Action) {
        when (action) {
            TagsOverviewViewModelActions.Action.OnCloseScreen -> onCloseScreen()
            TagsOverviewViewModelActions.Action.OnTryAgain -> onTryAgain()
            is TagsOverviewViewModelActions.Action.OnTagDetailClicked -> onTagDetailClicked(action.tagUuid)
            TagsOverviewViewModelActions.Action.OnRegisterNewTag -> onRegisterNewTag()
        }
    }

    private fun onCloseScreen() {
        launchIO {
            _uiEffect.emit(TagsOverviewUiEffect.CloseScreen)
        }
    }

    private fun onTryAgain() {
        fetchTags()
    }

    private fun onTagDetailClicked(tagUuid: String) {
        launchIO {
            _uiEffect.emit(TagsOverviewUiEffect.OpenTagDetail(tagUuid))
        }
    }

    private fun onRegisterNewTag() {
        launchIO {
            _uiEffect.emit(TagsOverviewUiEffect.AddNewTag)
        }
    }

    private fun fetchTags() {
        launchIO {
            getAllTagsUseCase()
                .onSuccess {
                    withContext(main()) {
                        val state = if (it.isEmpty()) {
                            TagsOverviewUiState.EmptyList(uiState.value.uiModel)
                        } else {
                            TagsOverviewUiState.Resume(uiState.value.uiModel.copy(tags = it))
                        }
                        _uiState.value = state
                    }
                }.onFailure {
                    _uiState.value =
                        TagsOverviewUiState.Error(
                            uiState.value.uiModel,
                            R.string.get_all_tag_suggestion_failed
                        )
                }
        }
    }
}
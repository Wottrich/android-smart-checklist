package wottrich.github.io.smartchecklist.suggestion.presentation.state

interface TagsOverviewViewModelActions {
    fun sendAction(action: Action)

    sealed class Action {
        object OnCloseScreen : Action()
        object OnTryAgain : Action()
        object OnRegisterNewTag : Action()
        data class OnTagDetailClicked(val tagUuid: String) : Action()
    }
}
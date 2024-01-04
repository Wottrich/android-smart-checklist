package wottrich.github.io.smartchecklist.presentation.ui.sort

interface SortTaskListAction {
    fun sendAction(action: Action)
    sealed class Action {
        data class OnSelectedItem(val sortItem: SortItem) : Action()
    }
}
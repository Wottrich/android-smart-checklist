package wottrich.github.io.smartchecklist.presentation.viewmodel

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import wottrich.github.io.smartchecklist.android.BaseViewModel

class EditChecklistTagContentViewModel : BaseViewModel(), EditChecklistTagContentAction {

    private var animationJob: Job? = null

    private val _state = MutableStateFlow(EditChecklistTagContentUiModel())
    val state = _state.asStateFlow()

    override fun sendAction(action: EditChecklistTagContentAction.Action) {
        when (action) {
            EditChecklistTagContentAction.Action.OnStartEditLabelAnimation -> onStartEditLabelAnimation()
            EditChecklistTagContentAction.Action.OnStopEditLabelAnimation -> onStopEditLabelAnimation()
        }
    }

    private fun onStartEditLabelAnimation() {
        animationJob = launchIO {
            _state.emit(state.value.copy(showEditLabel = true))
            delay(3000)
            _state.emit(state.value.copy(showEditLabel = false))
        }
    }

    private fun onStopEditLabelAnimation() {
        animationJob?.cancel()
    }


}

interface EditChecklistTagContentAction {
    fun sendAction(action: Action)
    sealed class Action {
        object OnStartEditLabelAnimation : Action()
        object OnStopEditLabelAnimation : Action()
    }
}

data class EditChecklistTagContentUiModel(
    val showEditLabel: Boolean = false
)
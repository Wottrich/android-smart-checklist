package wottrich.github.io.smartchecklist.newchecklist.presentation.viewmodels

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import wottrich.github.io.smartchecklist.android.BaseViewModel
import wottrich.github.io.smartchecklist.checklist.domain.UpdateSelectedChecklistUseCase
import wottrich.github.io.smartchecklist.coroutines.dispatcher.DispatchersProviders
import wottrich.github.io.smartchecklist.datasource.data.model.Checklist
import wottrich.github.io.smartchecklist.kotlin.SingleShotEventBus
import wottrich.github.io.smartchecklist.newchecklist.domain.AddNewChecklistUseCase
import wottrich.github.io.smartchecklist.newchecklist.presentation.states.NewChecklistNameUiEffect
import wottrich.github.io.smartchecklist.newchecklist.presentation.states.NewChecklistNameUiState

class NewChecklistNameViewModel(
    dispatchersProviders: DispatchersProviders,
    private val addNewChecklistUseCase: AddNewChecklistUseCase,
    private val updateSelectedChecklistUseCase: UpdateSelectedChecklistUseCase
) : BaseViewModel(dispatchersProviders) {

    private val _effects = SingleShotEventBus<NewChecklistNameUiEffect>()
    val effects: Flow<NewChecklistNameUiEffect> = _effects.events

    private val _state = MutableStateFlow(NewChecklistNameUiState.Initial)
    val state = _state.asStateFlow()

    fun onTextChange(text: String) {
        val isDoneButtonEnabled = text.isNotEmpty()
        _state.value = state.value.copy(
            checklistName = text,
            isDoneButtonEnabled = isDoneButtonEnabled
        )
    }

    fun onDoneButtonClicked() {
        createNewChecklistAndUpdateSelected()
    }

    private fun createNewChecklistAndUpdateSelected() {
        launchIO {
            val checklistName = state.value.checklistName
            val checklist = Checklist(name = checklistName)
            addNewChecklistUseCase(checklist).getOrNull()
            updateSelectedChecklistUseCase(checklist.uuid)
            _effects.emit(NewChecklistNameUiEffect.CloseScreen)
        }
    }

}
package github.io.wottrich.newchecklist.presentation.viewmodels

import github.io.wottrich.newchecklist.domain.AddNewChecklistUseCase
import github.io.wottrich.newchecklist.domain.UpdateSelectedChecklistUseCase
import github.io.wottrich.newchecklist.presentation.states.NewChecklistNameUiEffect
import github.io.wottrich.newchecklist.presentation.states.NewChecklistNameUiState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import wottrich.github.io.datasource.entity.NewChecklist
import wottrich.github.io.tools.SingleShotEventBus
import wottrich.github.io.tools.base.BaseViewModel
import wottrich.github.io.tools.dispatcher.DispatchersProviders

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
            val newChecklist = NewChecklist(name = checklistName)
            addNewChecklistUseCase(newChecklist).getOrNull()
            updateSelectedChecklistUseCase(newChecklist)
            _effects.emit(NewChecklistNameUiEffect.CloseScreen)
        }
    }

}
package github.io.wottrich.checklist.presentation.viewmodel

import github.io.wottrich.checklist.domain.usecase.GetAddChecklistUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import wottrich.github.io.tools.SingleShotEventBus
import wottrich.github.io.tools.base.BaseViewModel
import wottrich.github.io.tools.dispatcher.DispatchersProviders

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 13/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */

class ChecklistNameViewModel(
    dispatchers: DispatchersProviders,
    private val getAddChecklistUseCase: GetAddChecklistUseCase
) : BaseViewModel(dispatchers) {

    private val pendingActions = MutableSharedFlow<ChecklistNameActions>()

    private val _effects = SingleShotEventBus<ChecklistNameUiEffects>()
    val effects: Flow<ChecklistNameUiEffects> = _effects.events

    private val _state = MutableStateFlow<ChecklistNameState>(ChecklistNameState.Initial)
    val state = _state.asStateFlow()

    init {
        launchIO {
            pendingActions.collect { action ->
                when (action) {
                    ChecklistNameActions.ConfirmButtonAction -> createChecklist()
                }
            }
        }
    }

    fun onTextChange(text: String) {
        val isConfirmButtonEnabled = text.isNotEmpty()
        _state.value = state.value.copy(
            checklistName = text,
            isConfirmButtonEnabled = isConfirmButtonEnabled
        )
    }

    fun onConfirmButtonClicked() {
        launchIO {
            pendingActions.emit(ChecklistNameActions.ConfirmButtonAction)
        }
    }

    private fun createChecklist() {
        launchIO {
            val checklistName = state.value.checklistName
            val itemId = getAddChecklistUseCase(checklistName)
            if (itemId != null) {
                _effects.emit(ChecklistNameUiEffects.NextScreen(itemId.toString()))
            } else {
                _effects.emit(ChecklistNameUiEffects.InvalidChecklistState)
            }
        }
    }
}

data class ChecklistNameState(
    val checklistName: String,
    val isConfirmButtonEnabled: Boolean
) {
    companion object {
        val Initial = ChecklistNameState(checklistName = "", isConfirmButtonEnabled = false)
    }
}

sealed class ChecklistNameActions {
    object ConfirmButtonAction : ChecklistNameActions()
}

sealed class ChecklistNameUiEffects {
    data class NextScreen(val checklistId: String) : ChecklistNameUiEffects()
    object InvalidChecklistState : ChecklistNameUiEffects()
}
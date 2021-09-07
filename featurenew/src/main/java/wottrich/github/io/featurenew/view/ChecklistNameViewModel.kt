package wottrich.github.io.featurenew.view

import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import wottrich.github.io.database.dao.ChecklistDao
import wottrich.github.io.database.entity.Checklist
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
    private val dispatchersProviders: DispatchersProviders,
    private val database: ChecklistDao
) : ViewModel() {

    private val _action = MutableLiveData<ChecklistNameAction>()
    val action: LiveData<ChecklistNameAction> = _action

    private val _screenState: MutableStateFlow<ChecklistNameScreenState> =
        MutableStateFlow(ChecklistNameScreenState.InitialState)
    val screenState: StateFlow<ChecklistNameScreenState> = _screenState

    private fun canContinue(checklistName: String): Boolean = checklistName.isNotEmpty()

    fun nextScreen(checklistName: String) {
        if (canContinue(checklistName)) {
            viewModelScope.launch(dispatchersProviders.io) {
                val itemId = database.insert(Checklist(name = checklistName))
                if (itemId != null) {
                    _screenState.value = ChecklistNameScreenState.NextScreen(itemId.toString())
                } else {
                    _screenState.value = ChecklistNameScreenState.CheckListNotCreated
                    //_action.postValue(ChecklistNameAction.ErrorMessage(R.string.unknown))
                }
            }
        } else {
            _screenState.value = ChecklistNameScreenState.InvalidChecklistName
            //_action.postValue(ChecklistNameAction.ErrorMessage(R.string.fragment_new_checklist_error_continue))
        }
    }

}

sealed class ChecklistNameScreenState {

    object InitialState : ChecklistNameScreenState()
    data class NextScreen(val checklistId: String) : ChecklistNameScreenState()
    object CheckListNotCreated : ChecklistNameScreenState()
    object InvalidChecklistName : ChecklistNameScreenState()

}


sealed class ChecklistNameAction {
    data class NextScreen(val checklistId: Long) : ChecklistNameAction()
    data class ErrorMessage(@StringRes val stringRes: Int) : ChecklistNameAction()
}
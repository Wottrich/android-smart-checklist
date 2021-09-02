package wottrich.github.io.androidsmartchecklist.view

import androidx.lifecycle.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import wottrich.github.io.database.dao.ChecklistDao
import wottrich.github.io.database.entity.Checklist
import wottrich.github.io.tools.dispatcher.DispatchersProviders

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 12/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */

class HomeViewModel(
    private val dao: ChecklistDao,
    private val dispatchers: DispatchersProviders
) : ViewModel() {

    private val _homeStateFlow = MutableStateFlow(HomeState())
    val homeStateFlow: StateFlow<HomeState> = _homeStateFlow

    val checklists: LiveData<List<Checklist>> by lazy {
        return@lazy dao.selectAllFromChecklist().asLiveData(dispatchers.io)
    }

    init {
        viewModelScope.launch(dispatchers.io) {
            dao.selectAllFromChecklist().collect {
                _homeStateFlow.value = homeStateFlow.value.copy(isLoading = false, checklists = it)
            }
        }
    }

}

data class HomeState(
    val isLoading: Boolean = true,
    val checklists: List<Checklist> = emptyList()
)

sealed class HomeState2 {
    object Loading : HomeState2()
    data class Success(val checklists: List<Checklist>) : HomeState2()
    data class Error(val refreshing: Boolean = false) : HomeState2()
}
package wottrich.github.io.androidsmartchecklist.view

import androidx.lifecycle.*
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

    val checklists: LiveData<List<Checklist>> by lazy {
        return@lazy dao.selectAllFromChecklist().asLiveData(dispatchers.io)
    }

    init {
        checklists.value
    }

}
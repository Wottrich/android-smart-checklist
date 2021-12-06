package wottrich.github.io.featurenew.view.screens.checklistdetail

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 21/10/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

//class ChecklistDetailViewModel(
//    taskDao: TaskDao,
//    private val checklistId: String,
//    private val dispatchersProviders: DispatchersProviders,
//    private val checklistDao: ChecklistDao
//) : TaskListViewModel(checklistId, dispatchersProviders, taskDao) {
//
//    private val _stateScreen =
//        MutableStateFlow<TaskListState>(TaskListState.Overview)
//
//    val stateScreen = _stateScreen.asStateFlow()
//
//    fun changeState(state: TaskListState) {
//        _stateScreen.value = state
//    }
//
//    fun deleteChecklist() {
//        viewModelScope.launch(dispatchersProviders.io) {
//            val checklist = checklistDao.getChecklist(checklistId)
//            checklistDao.delete(checklist)
//            changeState(TaskListState.Delete)
//        }
//    }
//
//}
//
sealed class TaskListState {
    object Overview : TaskListState()
    object Edit : TaskListState()
    object Delete : TaskListState()
}
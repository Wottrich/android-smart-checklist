package wottrich.github.io.featurenew.view.task

import androidx.annotation.StringRes
import androidx.lifecycle.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import wottrich.github.io.database.dao.ChecklistDao
import wottrich.github.io.database.dao.TaskDao
import wottrich.github.io.database.entity.Task
import wottrich.github.io.featurenew.R
import wottrich.github.io.tools.dispatcher.DispatchersProviders
import wottrich.github.io.tools.livedata.SingleLiveEvent

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 21/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */
 
class TaskListViewModel(
    private val checklistId: Long,
    private val dispatchersProviders: DispatchersProviders,
    private val checklistDao: ChecklistDao,
    private val taskDao: TaskDao
) : ViewModel() {

    val taskName = MutableLiveData<String>()

    private var tasks = mutableListOf<Task>()

    private val _action = SingleLiveEvent<TaskListAction>()
    val action: LiveData<TaskListAction> = _action

    private val _taskList = MutableLiveData<List<Task>>()

    fun getLiveDataTaskList(): LiveData<List<Task>> {
        if (_taskList.value == null) {
            loadTaskListFromDatabase()
        }

        return _taskList
    }

    private fun loadTaskListFromDatabase() {
        viewModelScope.launch(dispatchersProviders.io) {
            checklistDao.selectAllFromChecklistWhereChecklistIdIs(checklistId).collect {
                val tasks = it.tasks
                this@TaskListViewModel.tasks = tasks.toMutableList()
                _taskList.postValue(tasks)
            }
        }
    }

    fun verifyTaskNameToAddItem() {
        val taskName = taskName.value
        if (!taskName.isNullOrEmpty()) {
            addTaskAndClearText(taskName)
        } else {
            _action.postValue(TaskListAction.ErrorMessage(R.string.fragment_task_list_error_add_item))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch(dispatchersProviders.io) {
            val newTaskList = tasks.apply {
                remove(task)
            }
            _action.postValue(TaskListAction.UpdateTaskList(newTaskList))
            taskDao.delete(task)
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch(dispatchersProviders.io) {
            tasks.find { it.taskId == task.taskId }?.let { taskToUpdate ->
                taskToUpdate.isCompleted = !task.isCompleted
                val newTaskList = tasks.apply {
                    val index = indexOf(task)
                    this[index] = taskToUpdate
                }
                taskDao.update(task)
                _action.postValue(TaskListAction.UpdateTaskList(newTaskList))
            }
        }
    }

    fun onCompleteItemClicked() {

    }

    private fun addTaskAndClearText(taskName: String) {
        viewModelScope.launch(dispatchersProviders.io) {
            val task = generateTask(taskName)
            val taskId = taskDao.insert(task)
            task.taskId = taskId
            val newTaskList = tasks.apply {
                add(task)
            }
            _action.postValue(TaskListAction.UpdateTaskList(newTaskList))
        }
        clearTaskName()
    }

    private fun generateTask(taskName: String): Task {
        return Task(checklistId = checklistId, name = taskName)
    }

    private fun clearTaskName() {
        taskName.value = ""
    }

}

sealed class TaskListAction {
    data class UpdateTaskList(val taskList: List<Task>) : TaskListAction()
    data class ErrorMessage(@StringRes val stringRes: Int) : TaskListAction()
}
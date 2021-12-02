package wottrich.github.io.featurenew.view.screens.tasklist

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import wottrich.github.io.database.dao.TaskDao
import wottrich.github.io.database.entity.Task
import wottrich.github.io.tools.dispatcher.DispatchersProviders

open class TaskListViewModel(
    private val checklistId: String,
    private val dispatchersProviders: DispatchersProviders,
    private val taskDao: TaskDao
) : ViewModel() {

    var tasks = mutableStateListOf<Task>()
        private set

    init {
        viewModelScope.launch(dispatchersProviders.main) {
            val loadedTasks = taskDao.getTasks(checklistId)
            tasks.addAll(loadedTasks)
        }
    }

    fun onAddClicked(taskName: String) {
        addTaskAndClearText(taskName)
    }

    fun onDeleteClicked(task: Task) {
        viewModelScope.launch(dispatchersProviders.io) {
            tasks.remove(task)
            taskDao.delete(task)
        }
    }

    fun onUpdateClicked(task: Task) {
        viewModelScope.launch(dispatchersProviders.io) {
            tasks.find { it.taskId == task.taskId }?.let { taskToUpdate ->
                taskToUpdate.isCompleted = !task.isCompleted
                val indexOfUpdateTask = tasks.indexOf(task)
                tasks[indexOfUpdateTask] = taskToUpdate
                taskDao.update(task)
            }
        }
    }

    private fun addTaskAndClearText(taskName: String) {
        viewModelScope.launch(dispatchersProviders.io) {
            val task = generateTask(taskName)
            val taskId = taskDao.insert(task)
            task.taskId = taskId
            tasks.add(task)
        }
    }

    private fun generateTask(taskName: String): Task {
        return Task(checklistId = checklistId.toLong(), name = taskName)
    }

}
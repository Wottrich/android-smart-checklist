package wottrich.github.io.featurenew.view.screens.tasklist

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import wottrich.github.io.database.entity.Task
import wottrich.github.io.featurenew.domain.usecase.GetAddTaskUseCase
import wottrich.github.io.featurenew.domain.usecase.GetChangeTaskStatusUseCase
import wottrich.github.io.featurenew.domain.usecase.GetDeleteTaskUseCase
import wottrich.github.io.featurenew.domain.usecase.GetLoadTaskUseCase
import wottrich.github.io.tools.dispatcher.DispatchersProviders

open class TaskListViewModel(
    private val checklistId: String,
    private val dispatchersProviders: DispatchersProviders,
    private val getLoadTaskUseCase: GetLoadTaskUseCase,
    private val getAddTaskUseCase: GetAddTaskUseCase,
    private val getDeleteTaskUseCase: GetDeleteTaskUseCase,
    private val getChangeTaskStatusUseCase: GetChangeTaskStatusUseCase,
) : ViewModel() {

    var tasks = mutableStateListOf<Task>()
        private set

    init {
        viewModelScope.launch(dispatchersProviders.io) {
            getLoadTaskUseCase(checklistId).collect {
                withContext(dispatchersProviders.main) {
                    tasks.clear()
                    tasks.addAll(it)
                }
            }
        }
    }

    fun onAddClicked(taskName: String) {
        addTaskAndClearText(taskName)
    }

    fun onDeleteClicked(task: Task) {
        viewModelScope.launch(dispatchersProviders.io) {
            getDeleteTaskUseCase(task)
        }
    }

    fun onUpdateClicked(task: Task) {
        viewModelScope.launch(dispatchersProviders.io) {
            getChangeTaskStatusUseCase(task)
        }
    }

    private fun addTaskAndClearText(taskName: String) {
        viewModelScope.launch(dispatchersProviders.io) {
            getAddTaskUseCase(checklistId.toLong(), taskName)
        }
    }

}
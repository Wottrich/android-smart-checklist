package wottrich.github.io.publicandroid.presentation.viewmodel

import github.io.wottrich.test.tools.BaseUnitTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Test
import wottrich.github.io.database.entity.Task
import wottrich.github.io.publicandroid.domain.usecase.GetAddTaskUseCase
import wottrich.github.io.publicandroid.domain.usecase.GetChangeTaskStatusUseCase
import wottrich.github.io.publicandroid.domain.usecase.GetDeleteTaskUseCase
import wottrich.github.io.publicandroid.domain.usecase.ObserveTasksUseCase

class TaskListViewModelTest : BaseUnitTest() {

    private lateinit var sut: TaskListViewModel
    private lateinit var observeTasksUseCase: ObserveTasksUseCase
    private lateinit var getAddTaskUseCase: GetAddTaskUseCase
    private lateinit var getDeleteTaskUseCase: GetDeleteTaskUseCase
    private lateinit var getChangeTaskStatusUseCase: GetChangeTaskStatusUseCase

    private val dummyChecklistId = "0"
    private val dummyTask = Task(
        taskId = 0,
        checklistId = dummyChecklistId.toLong(),
        name = ""
    )
    private val dummyTasks = listOf<Task>(
        dummyTask.copy(taskId = 0, name = "task1"),
        dummyTask.copy(taskId = 1, name = "task2")
    )

    @Before
    override fun setUp() {
        observeTasksUseCase = mockk()
        getAddTaskUseCase = mockk()
        getDeleteTaskUseCase = mockk()
        getChangeTaskStatusUseCase = mockk()
    }

    @Test
    fun `GIVEN viewmodel initialized WHEN load tasks is requested THEN must update tasks`() {
        mockGetLoadTaskUseCase()
        sut = getSut()
        assertEquals(dummyTasks, sut.tasks)
        verify(exactly = 1) { observeTasksUseCase.invoke(dummyChecklistId) }
    }

    @Test
    fun `GIVEN tasks loaded WHEN user add new task THEN must call add task usecase`() {
        mockGetLoadTaskUseCase()
        mockAddTaskUseCase()
        sut = getSut()

        sut.onAddClicked("task3")

        coVerify(exactly = 1) { getAddTaskUseCase.invoke(dummyChecklistId.toLong(), "task3") }
    }

    @Test
    fun `GIVEN tasks loaded WHEN user delete task THEN must call delete task usecase`() {
        val deletedTask = dummyTasks.first()
        mockGetLoadTaskUseCase()
        mockGetDeleteTaskUseCase(deletedTask)
        sut = getSut()

        sut.onDeleteClicked(deletedTask)

        coVerify(exactly = 1) { getDeleteTaskUseCase.invoke(deletedTask) }
    }

    @Test
    fun `GIVEN tasks loaded WHEN user update task status THEN must call update task usecase and must notify load task use case`() {
        val updatedTask = dummyTasks.first()
        mockGetLoadTaskUseCase()
        mockGetChangeTaskStatusUseCase()
        sut = getSut()

        sut.onUpdateClicked(updatedTask)

        coVerify(exactly = 1) { getChangeTaskStatusUseCase.invoke(updatedTask) }
    }

    private fun mockGetLoadTaskUseCase() {
        coEvery { observeTasksUseCase.invoke(dummyChecklistId) } returns flow {
            emit(dummyTasks)
        }
    }

    private fun mockAddTaskUseCase() {
        coEvery { getAddTaskUseCase.invoke(dummyChecklistId.toLong(), any()) } returns
                dummyTask.copy(taskId = 2, name = "task3")
    }

    private fun mockGetDeleteTaskUseCase(task: Task = dummyTasks.first()) {
        coEvery { getDeleteTaskUseCase.invoke(task) } returns Unit
    }

    private fun mockGetChangeTaskStatusUseCase(task: Task = dummyTasks.first()) {
        coEvery { getChangeTaskStatusUseCase.invoke(task) } returns Unit
    }

    private fun getSut() = TaskListViewModel(
        dummyChecklistId,
        coroutinesTestRule.dispatchers,
        observeTasksUseCase,
        getAddTaskUseCase,
        getDeleteTaskUseCase,
        getChangeTaskStatusUseCase
    )
}
package wottrich.github.io.androidsmartchecklist.presentation.viewmodel

import github.io.wottrich.checklist.domain.usecase.DeleteChecklistUseCase
import github.io.wottrich.checklist.domain.usecase.GetChecklistWithTaskUseCase
import github.io.wottrich.checklist.domain.usecase.UpdateSelectedChecklistUseCase
import github.io.wottrich.test.tools.BaseUnitTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.junit.Test
import wottrich.github.io.datasource.entity.NewChecklist
import wottrich.github.io.datasource.entity.NewChecklistWithNewTasks
import wottrich.github.io.datasource.entity.NewTask
import github.io.wottrich.coroutines.base.Result
import wottrich.github.io.tools.base.successEmptyResult

class HomeDrawerViewModelTest : BaseUnitTest() {

    private lateinit var sut: HomeDrawerViewModel
    private lateinit var getChecklistWithTaskUseCase: GetChecklistWithTaskUseCase
    private lateinit var updateSelectedChecklistUseCase: UpdateSelectedChecklistUseCase
    private lateinit var deleteChecklistUseCase: DeleteChecklistUseCase

    private val dummyChecklistId = "0"
    private val dummyTask = NewTask(
        uuid = "0",
        parentUuid = dummyChecklistId,
        name = ""
    )
    private val dummyChecklist = NewChecklist(uuid = dummyChecklistId, name = "Checklist1")
    private val dummyChecklistWithTasks = NewChecklistWithNewTasks(
        dummyChecklist,
        listOf(
            dummyTask.copy(uuid = "0", name = "task1"),
            dummyTask.copy(uuid = "1", name = "task2"),
            dummyTask.copy(uuid = "2", name = "task3"),
        )
    )

    private val dummyListOfChecklistWithTasks = listOf(
        dummyChecklistWithTasks,
        dummyChecklistWithTasks.copy(
            newChecklist = dummyChecklist.copy(uuid = "1", name = "Checklist2"),
            listOf(
                dummyTask.copy(parentUuid = "1", uuid = "4", name = "task2"),
                dummyTask.copy(parentUuid = "1", uuid = "3", name = "task1"),
                dummyTask.copy(parentUuid = "1", uuid = "5", name = "task3"),
            )
        )
    )

    override fun setUp() {
        getChecklistWithTaskUseCase = mockk()
        updateSelectedChecklistUseCase = mockk()
        deleteChecklistUseCase = mockk()
    }

    @Test
    fun `GIVEN ViewModel initialized WHEN checklist use case is called THEN must return list of checklist`() {
        mockGetChecklistWithTaskUseCase()
        sut = getSut()
        val value = getSuspendValue { sut.drawerStateFlow.first() as HomeDrawerState.Content }
        assertEquals(dummyListOfChecklistWithTasks, value.checklists)
    }

    @Test
    fun `GIVEN user wants edit lists of checklist WHEN user click to edit THEN state must change to edit mode`() {
        mockGetChecklistWithTaskUseCase()
        sut = getSut()
        sut.processEvent(HomeDrawerEvent.EditModeClicked)

        val value = getSuspendValue { sut.drawerStateFlow.first() as HomeDrawerState.Content }
        assertTrue(value.isEditing)
    }

    @Test
    fun `GIVEN another checklist selected WHEN user select another checklist THEN must call use case to change selected checklist`() {
        mockGetChecklistWithTaskUseCase()
        mockGetUpdateSelectedChecklistUseCase(dummyChecklistWithTasks.newChecklist)
        sut = getSut()
        sut.processEvent(HomeDrawerEvent.ItemClicked(dummyChecklistWithTasks))

        coVerify(exactly = 1) {
            updateSelectedChecklistUseCase(dummyChecklistWithTasks.newChecklist)
        }
        val effect = getSuspendValue { sut.drawerEffectFlow.first() }
        assertTrue(effect is HomeDrawerEffect.CloseDrawer)
    }

    @Test
    fun `GIVEN some checklist WHEN user delete this checklist THEN must call use case to delete checklist`() {
        mockGetChecklistWithTaskUseCase()
        mockGetDeleteChecklistUseCase(dummyChecklistWithTasks.newChecklist)
        sut = getSut()
        sut.processEvent(HomeDrawerEvent.DeleteChecklistClicked(dummyChecklistWithTasks))

        coVerify(exactly = 1) {
            deleteChecklistUseCase(dummyChecklistWithTasks.newChecklist)
        }
    }

    private fun mockGetChecklistWithTaskUseCase() {
        coEvery { getChecklistWithTaskUseCase.invoke() } returns flow {
            emit(Result.success(dummyListOfChecklistWithTasks))
        }
    }

    private fun mockGetUpdateSelectedChecklistUseCase(checklist: NewChecklist) {
        coEvery { updateSelectedChecklistUseCase.invoke(checklist) } returns successEmptyResult()
    }

    private fun mockGetDeleteChecklistUseCase(checklist: NewChecklist) {
        coEvery { deleteChecklistUseCase.invoke(checklist) } returns successEmptyResult()
    }


    private fun getSut() = HomeDrawerViewModel(
        coroutinesTestRule.dispatchers,
        getChecklistWithTaskUseCase,
        updateSelectedChecklistUseCase,
        deleteChecklistUseCase
    )

}
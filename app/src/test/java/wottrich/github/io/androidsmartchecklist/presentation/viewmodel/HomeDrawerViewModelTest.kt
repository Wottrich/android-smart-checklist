package wottrich.github.io.androidsmartchecklist.presentation.viewmodel

import github.io.wottrich.checklist.domain.DeleteChecklistUseCase
import github.io.wottrich.checklist.domain.UpdateSelectedChecklistUseCase
import github.io.wottrich.coroutines.base.Result
import github.io.wottrich.coroutines.successEmptyResult
import github.io.wottrich.test.tools.BaseUnitTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.junit.Test
import wottrich.github.io.androidsmartchecklist.domain.usecase.GetChecklistDrawerUseCase
import wottrich.github.io.androidsmartchecklist.presentation.ui.model.HomeDrawerChecklistItemModel
import wottrich.github.io.datasource.entity.NewChecklist

class HomeDrawerViewModelTest : BaseUnitTest() {

    private lateinit var sut: HomeDrawerViewModel
    private lateinit var getChecklistDrawerUseCase: GetChecklistDrawerUseCase
    private lateinit var updateSelectedChecklistUseCase: UpdateSelectedChecklistUseCase
    private lateinit var deleteChecklistUseCase: DeleteChecklistUseCase

    private val dummyChecklistId = "0"
    private val dummyChecklist = NewChecklist(uuid = dummyChecklistId, name = "Checklist1")
    private val dummyChecklistWithTasks = HomeDrawerChecklistItemModel(
        dummyChecklist.uuid,
        dummyChecklist.name,
        completeCountLabel = "1 / 2",
        isChecklistSelected = dummyChecklist.isSelected,
        hasUncompletedTask = true
    )

    private val dummyListOfChecklistWithTasks = listOf(
        dummyChecklistWithTasks,
        dummyChecklistWithTasks.copy(
            checklistUuid = "1",
            checklistName = "Checklist2",
            completeCountLabel = "2 / 2",
            isChecklistSelected = false,
            hasUncompletedTask = false
        )
    )

    override fun setUp() {
        getChecklistDrawerUseCase = mockk()
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
        mockGetUpdateSelectedChecklistUseCase(dummyChecklistWithTasks.checklistUuid)
        sut = getSut()
        sut.processEvent(HomeDrawerEvent.ItemClicked(dummyChecklistWithTasks))

        coVerify(exactly = 1) {
            updateSelectedChecklistUseCase(dummyChecklistWithTasks.checklistUuid)
        }
        val effect = getSuspendValue { sut.drawerEffectFlow.first() }
        assertTrue(effect is HomeDrawerEffect.CloseDrawer)
    }

    @Test
    fun `GIVEN some checklist WHEN user delete this checklist THEN must call use case to delete checklist`() {
        mockGetChecklistWithTaskUseCase()
        mockGetDeleteChecklistUseCase(dummyChecklistWithTasks.checklistUuid)
        sut = getSut()
        sut.processEvent(HomeDrawerEvent.DeleteChecklistClicked(dummyChecklistWithTasks))

        coVerify(exactly = 1) {
            deleteChecklistUseCase(dummyChecklistWithTasks.checklistUuid)
        }
    }

    private fun mockGetChecklistWithTaskUseCase() {
        coEvery { getChecklistDrawerUseCase.invoke() } returns flow {
            emit(Result.success(dummyListOfChecklistWithTasks))
        }
    }

    private fun mockGetUpdateSelectedChecklistUseCase(checklistUuid: String) {
        coEvery { updateSelectedChecklistUseCase.invoke(checklistUuid) } returns successEmptyResult()
    }

    private fun mockGetDeleteChecklistUseCase(checklistUuid: String) {
        coEvery { deleteChecklistUseCase.invoke(checklistUuid) } returns successEmptyResult()
    }


    private fun getSut() = HomeDrawerViewModel(
        coroutinesTestRule.dispatchers,
        getChecklistDrawerUseCase,
        updateSelectedChecklistUseCase,
        deleteChecklistUseCase
    )

}
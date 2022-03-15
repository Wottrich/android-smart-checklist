package github.io.wottrich.checklist.presentation.viewmodel

import github.io.wottrich.checklist.domain.usecase.GetAddChecklistUseCase
import github.io.wottrich.test.tools.BaseUnitTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import kotlinx.coroutines.flow.firstOrNull
import org.junit.Before
import org.junit.Test

class ChecklistNameViewModelTest : BaseUnitTest() {

    private lateinit var sut: ChecklistNameViewModel

    private lateinit var getAddChecklistUseCase: GetAddChecklistUseCase

    @Before
    override fun setUp() {
        getAddChecklistUseCase = mockk()
        sut = ChecklistNameViewModel(
            coroutinesTestRule.dispatchers,
            getAddChecklistUseCase
        )
    }

    @Test
    fun `WHEN text is changed THEN must update state with new state`() {
        val expectedText = "new text"
        val expectedButtonState = true
        sut.onTextChange(expectedText)

        assertEquals(expectedText, sut.state.value.checklistName)
        assertEquals(expectedButtonState, sut.state.value.isConfirmButtonEnabled)
    }

    @Test
    fun `WHEN text is empty THEN must update state with button state disabled`() {
        val expectedText = ""
        val expectedButtonState = false
        sut.onTextChange(expectedText)

        assertEquals(expectedText, sut.state.value.checklistName)
        assertEquals(expectedButtonState, sut.state.value.isConfirmButtonEnabled)
    }

    @Test
    fun `GIVEN item id not null WHEN on confirm button is clicked THEN create checklist must be called`() = runBlockingUnitTest {
        val expectedChecklistId = 0L
        val expectedName = "Name test"
        coEvery { getAddChecklistUseCase.invoke(any()) } returns expectedChecklistId

        sut.onTextChange(expectedName)
        sut.onConfirmButtonClicked()

        coVerify(exactly = 1) { getAddChecklistUseCase.invoke(expectedName) }

        val value = sut.effects.firstOrNull() as? ChecklistNameUiEffects.NextScreen
        assertNotNull(value)
        assertEquals(expectedChecklistId.toString(), value!!.checklistId)
    }

    @Test
    fun `GIVEN item id null WHEN on confirm button is clicked THEN effects must notify invalid actions`() = runBlockingUnitTest {
        val expectedChecklistId = 0L
        val expectedName = "Name test"
        coEvery { getAddChecklistUseCase.invoke(any()) } returns expectedChecklistId

        sut.onTextChange(expectedName)
        sut.onConfirmButtonClicked()

        coVerify(exactly = 1) { getAddChecklistUseCase.invoke(expectedName) }

        val value = sut.effects.firstOrNull() as? ChecklistNameUiEffects.NextScreen
        assertNotNull(value)
        assertEquals(expectedChecklistId.toString(), value!!.checklistId)
    }

}
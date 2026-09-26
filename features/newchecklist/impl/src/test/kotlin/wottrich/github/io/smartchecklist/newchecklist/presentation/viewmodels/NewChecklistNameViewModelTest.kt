package wottrich.github.io.smartchecklist.newchecklist.presentation.viewmodels

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import org.junit.Test
import wottrich.github.io.smartchecklist.checklist.domain.UpdateSelectedChecklistUseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.coroutines.failureEmptyResult
import wottrich.github.io.smartchecklist.coroutines.successEmptyResult
import wottrich.github.io.smartchecklist.newchecklist.domain.usecase.AddNewChecklistUseCase
import wottrich.github.io.smartchecklist.newchecklist.presentation.states.NewChecklistNameUiEffect
import wottrich.github.io.smartchecklist.newchecklist.presentation.states.NewChecklistNameUiState
import wottrich.github.io.smartchecklist.testtools.BaseUnitTest
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class NewChecklistNameViewModelTest : BaseUnitTest() {

    private lateinit var sut: NewChecklistNameViewModel
    private val addNewChecklistUseCase: AddNewChecklistUseCase = mockk()
    private val updateSelectedChecklistUseCase: UpdateSelectedChecklistUseCase = mockk()

    override fun setUp() {
        sut = NewChecklistNameViewModel(
            coroutinesTestRule.dispatchers,
            addNewChecklistUseCase,
            updateSelectedChecklistUseCase
        )
    }

    @Test
    fun `GIVEN nothing is texted WHEN viewmodel is alive THEM must return initial state`() =
        runBlockingUnitTest {
            val value = sut.state.first()
            assertEquals(NewChecklistNameUiState.Initial, value)
        }

    @Test
    fun `GIVEN something is texted WHEN viewmodel on text change is called THEN must return state with text and button enabled`() =
        runBlockingUnitTest {
            val expectedState = NewChecklistNameUiState("123", true)
            sut.onTextChange("123")
            val value = sut.state.first()
            assertEquals(expectedState, value)
        }

    @Test
    fun `GIVEN something is texted but deleted before WHEN viewmodel on text change is called THEN must return state with empty text and button disabled`() =
        runBlockingUnitTest {
            val expectedState = NewChecklistNameUiState("", false)
            sut.onTextChange("123")
            sut.onTextChange("")
            val value = sut.state.first()
            assertEquals(expectedState, value)
        }

    @Test
    fun `GIVEN all usecases returns success WHEN viewmodel done function called THEN must add new checklist, update and call effect`() =
        runBlockingUnitTest {
            coEvery { addNewChecklistUseCase.invoke(any()) } returns Result.success(0L)
            coEvery { updateSelectedChecklistUseCase.invoke(any()) } returns successEmptyResult()
            val expectedState = NewChecklistNameUiState("123", true)
            sut.onTextChange("123")
            val value = sut.state.first()
            assertEquals(expectedState, value)

            sut.onDoneButtonClicked()
            val effect = sut.effects.first()
            assertEquals(NewChecklistNameUiEffect.CloseScreen, effect)
        }

    @Test
    fun `GIVEN new checklist return error WHEN viewmodel done function called THEN must notify failure effect`() =
        runBlockingUnitTest {
            coEvery { addNewChecklistUseCase.invoke(any()) } returns Result.failure(RuntimeException("Error"))
            val expectedState = NewChecklistNameUiState("123", true)
            sut.onTextChange("123")
            val value = sut.state.first()
            assertEquals(expectedState, value)

            sut.onDoneButtonClicked()
            val effect = sut.effects.first()
            assertEquals(NewChecklistNameUiEffect.CreateChecklistFailed, effect)
            coVerify(inverse = true) { updateSelectedChecklistUseCase.invoke(any()) }
        }

    @Test
    fun `GIVEN update selected checklist returns error WHEN viewmodel done function called THEN must notify close screen effect`() =
        runBlockingUnitTest {
            coEvery { addNewChecklistUseCase.invoke(any()) } returns Result.success(0L)
            coEvery {
                updateSelectedChecklistUseCase.invoke(any())
            } returns failureEmptyResult(RuntimeException("Error"))
            val expectedState = NewChecklistNameUiState("123", true)
            sut.onTextChange("123")
            val value = sut.state.first()
            assertEquals(expectedState, value)

            sut.onDoneButtonClicked()
            val effect = sut.effects.first()
            assertEquals(NewChecklistNameUiEffect.CloseScreen, effect)
        }
}
package wottrich.github.io.smartchecklist.domain.usecase

import github.io.wottrich.test.tools.BaseUnitTest
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import org.junit.Test
import wottrich.github.io.smartchecklist.domain.mapper.SimpleChecklistModelMapper
import wottrich.github.io.smartchecklist.datasource.entity.NewChecklist
import wottrich.github.io.smartchecklist.datasource.entity.NewChecklistWithNewTasks
import wottrich.github.io.smartchecklist.datasource.entity.NewTask
import wottrich.github.io.smartchecklist.datasource.repository.ChecklistRepository
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class ObserveSimpleSelectedChecklistModelUseCaseTest : BaseUnitTest() {
    private lateinit var sut: ObserveSimpleSelectedChecklistModelUseCase
    private lateinit var checklistRepository: ChecklistRepository
    private val mapper: SimpleChecklistModelMapper = SimpleChecklistModelMapper()

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
            dummyTask,
            dummyTask,
            dummyTask
        )
    )

    override fun setUp() {
        checklistRepository = mockk()
        sut = ObserveSimpleSelectedChecklistModelUseCase(checklistRepository, mapper)
    }

    @OptIn(InternalCoroutinesApi::class)
    @Test
    fun `GIVEN selected checklist is null WHEN use case is requested THEN must returns error`() = runBlockingUnitTest {
        val localFlow = flow<NewChecklistWithNewTasks?> {
            emit(null)
        }
        every { checklistRepository.observeSelectedChecklistWithTasks() } returns localFlow

        sut().collect(
            FlowCollector {
                assertTrue(it.isFailure)
                assertTrue(it.exceptionOrNull() is NullPointerException)
            }
        )
    }

    @OptIn(InternalCoroutinesApi::class)
    @Test
    fun `GIVEN selected checklist is not null WHEN use case is requested THEN must returns simple checklist model`() = runBlockingUnitTest {
        val expectedChecklist = mapper.mapToSimpleChecklistModel(dummyChecklistWithTasks.newChecklist)
        val localFlow = flow<NewChecklistWithNewTasks?> {
            emit(dummyChecklistWithTasks)
        }
        every { checklistRepository.observeSelectedChecklistWithTasks() } returns localFlow

        sut().collect(
            FlowCollector {
                assertEquals(expectedChecklist, it.getOrNull())
            }
        )
    }
}
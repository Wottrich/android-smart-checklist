package wottrich.github.io.smartchecklist.domain.usecase

import github.io.wottrich.test.tools.BaseUnitTest
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Test
import wottrich.github.io.smartchecklist.domain.mapper.HomeDrawerChecklistItemModelMapper
import wottrich.github.io.smartchecklist.datasource.entity.NewChecklist
import wottrich.github.io.smartchecklist.datasource.entity.NewChecklistWithNewTasks
import wottrich.github.io.smartchecklist.datasource.entity.NewTask
import wottrich.github.io.smartchecklist.datasource.repository.ChecklistRepository
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class GetChecklistDrawerUseCaseTest : BaseUnitTest() {
    private lateinit var sut : GetChecklistDrawerUseCase
    private lateinit var checklistRepository: ChecklistRepository
    private val mapper: HomeDrawerChecklistItemModelMapper = HomeDrawerChecklistItemModelMapper()

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

    @Before
    override fun setUp() {
        checklistRepository = mockk()
        sut = GetChecklistDrawerUseCase(checklistRepository, mapper)
    }

    @OptIn(InternalCoroutinesApi::class)
    @Test
    fun `GIVEN has no registered checklists WHEN use case is requested THEN must return empty`() = runBlockingUnitTest {
        val expectedList = listOf<NewChecklistWithNewTasks>()
        val localFlow = flow {
            emit(expectedList)
        }
        every { checklistRepository.observeAllChecklistsWithTask() } returns localFlow

        sut().collect(
            FlowCollector {
                assertTrue(it.getOrNull()?.isEmpty()!!)
            }
        )
        verify { checklistRepository.observeAllChecklistsWithTask() }
    }

    @OptIn(InternalCoroutinesApi::class)
    @Test
    fun `GIVEN has registered checklists WHEN use case is requested THEN must return registered checklists`() = runBlockingUnitTest {
        val expectedList = listOf<NewChecklistWithNewTasks>(
            dummyChecklistWithTasks,
            dummyChecklistWithTasks,
        )
        val expectedMapped = expectedList.map {
            mapper.mapToHomeDrawerChecklistItemModelMapper(it)
        }
        val localFlow = flow {
            emit(expectedList)
        }
        every { checklistRepository.observeAllChecklistsWithTask() } returns localFlow

        sut().collect(
            FlowCollector {
                assertTrue(it.getOrNull()?.isNotEmpty()!!)
                assertEquals(expectedMapped, it.getOrNull())
            }
        )
        verify { checklistRepository.observeAllChecklistsWithTask() }
    }
}
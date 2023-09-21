package wottrich.github.io.smartchecklist.domain.usecase

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import wottrich.github.io.smartchecklist.datasource.entity.NewTask
import wottrich.github.io.smartchecklist.domain.model.SortItemType
import wottrich.github.io.smartchecklist.presentation.sort.model.TaskSortItemState
import wottrich.github.io.smartchecklist.task.R
import wottrich.github.io.smartchecklist.testtools.BaseUnitTest
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class SortTasksBySelectedSortUseCaseTest : BaseUnitTest() {

    private lateinit var sut: SortTasksBySelectedSortUseCase

    @Before
    override fun setUp() {
        sut = SortTasksBySelectedSortUseCase()
    }

    @Test
    fun `GIVEN has no sort item selected WHEN usecase is called THEN must returns tasks without sort`() =
        runBlockingUnitTest {
            val sortItems = buildSortItems()
            val tasks = buildTasksCompletedFirst()
            val tasksSorted =
                sut(SortTasksBySelectedSortUseCase.Params(sortItems, tasks)).getOrNull()
            assertEquals(tasks, tasksSorted)
        }

    @Test
    fun `GIVEN has uncompleted sort selected WHEN usecase is called THEN must returns uncompleted tasks first`() =
        runBlockingUnitTest {
            val sortItems = buildSortItems(SortItemType.UNCOMPLETED_TASKS)
            val tasks = buildTaskUncompletedFirst()
            val result = sut(SortTasksBySelectedSortUseCase.Params(sortItems, tasks))
            val tasksSorted = result.getOrNull()
            assertTrue(tasksSorted!!.first().isCompleted)
        }

    @Test
    fun `GIVEN has completed sort selected WHEN usecase is called THEN must returns completed tasks first`() =
        runBlockingUnitTest {
            val sortItems = buildSortItems(SortItemType.COMPLETED_TASKS)
            val tasks = buildTaskUncompletedFirst()
            val tasksSorted =
                sut(SortTasksBySelectedSortUseCase.Params(sortItems, tasks)).getOrNull()!!
            assertFalse(tasksSorted.first().isCompleted)
        }

    private fun buildTaskUncompletedFirst(): List<NewTask> {
        return listOf(
            NewTask(
                parentUuid = "123_parent",
                name = "First task",
                isCompleted = false
            ),
            NewTask(
                parentUuid = "123_parent",
                name = "Second task",
                isCompleted = true
            )
        )
    }

    private fun buildTasksCompletedFirst(): List<NewTask> {
        return listOf(
            NewTask(
                parentUuid = "123_parent",
                name = "First task",
                isCompleted = true
            ),
            NewTask(
                parentUuid = "123_parent",
                name = "Second task",
                isCompleted = false
            )
        )
    }

    private fun buildSortItems(selectedSortItem: SortItemType? = SortItemType.UNSELECTED_SORT): List<TaskSortItemState> {
        return listOf(
            TaskSortItemState(
                SortItemType.UNCOMPLETED_TASKS,
                R.string.task_sort_uncompleted_task_item,
                selectedSortItem == SortItemType.UNCOMPLETED_TASKS
            ),
            TaskSortItemState(
                SortItemType.COMPLETED_TASKS,
                R.string.task_sort_completed_task_item,
                selectedSortItem == SortItemType.COMPLETED_TASKS
            )
        )
    }

}
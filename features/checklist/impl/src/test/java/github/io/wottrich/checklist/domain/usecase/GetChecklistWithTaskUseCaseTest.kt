package github.io.wottrich.checklist.domain.usecase

import github.io.wottrich.test.tools.BaseUnitTest
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import org.junit.Before
import org.junit.Test
import wottrich.github.io.datasource.dao.ChecklistDao
import wottrich.github.io.datasource.entity.NewChecklist
import wottrich.github.io.datasource.entity.NewChecklistWithNewTasks
import wottrich.github.io.datasource.entity.NewTask

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 17/01/2022
 *
 * Copyright © 2022 AndroidSmartCheckList. All rights reserved.
 */

class GetChecklistWithTaskUseCaseTest : BaseUnitTest() {

    private lateinit var sut: GetChecklistWithTaskUseCase

    private lateinit var checklistDao: ChecklistDao

    @Before
    override fun setUp() {
        checklistDao = mockk()
        sut = GetChecklistWithTaskUseCase(checklistDao)
    }

    @OptIn(InternalCoroutinesApi::class)
    @Test
    fun `WHEN checklist with task is observed THEN must notify flow`() = runBlockingUnitTest {
        val expectedList = listOf(
            NewChecklistWithNewTasks(
                NewChecklist(uuid = "0", name = "checklist 1"),
                newTasks = listOf(NewTask(uuid = "0", name = "task 1", parentUuid = "0"))
            ),
            NewChecklistWithNewTasks(
                NewChecklist(uuid = "1", name = "checklist 2"),
                newTasks = listOf(NewTask(uuid = "1", name = "task 2", parentUuid = "1"))
            )
        )
        val localFlow = flow<List<NewChecklistWithNewTasks>> {
            emit(expectedList)
        }
        coEvery { checklistDao.observeChecklistsWithTaskUpdate() } returns localFlow

        sut().collect(
            FlowCollector {
                assertEquals(expectedList, it.getOrNull())
            }
        )
        verify { checklistDao.observeChecklistsWithTaskUpdate() }
    }

}
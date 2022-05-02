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
import wottrich.github.io.datasource.entity.Checklist
import wottrich.github.io.datasource.entity.ChecklistWithTasks
import wottrich.github.io.datasource.entity.Task

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
            ChecklistWithTasks(
                Checklist(checklistId = 0, name = "checklist 1"),
                tasks = listOf(Task(taskId = 0, name = "task 1"))
            ),
            ChecklistWithTasks(
                Checklist(checklistId = 1, name = "checklist 2"),
                tasks = listOf(Task(taskId = 1, name = "task 2"))
            )
        )
        val localFlow = flow<List<ChecklistWithTasks>> {
            emit(expectedList)
        }
        coEvery { checklistDao.observeChecklistsWithTaskUpdate() } returns localFlow

        sut().collect(
            FlowCollector {
                assertEquals(expectedList, it)
            }
        )
        verify { checklistDao.observeChecklistsWithTaskUpdate() }
    }

}
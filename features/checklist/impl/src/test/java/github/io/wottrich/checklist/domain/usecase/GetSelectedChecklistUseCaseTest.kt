package github.io.wottrich.checklist.domain.usecase

import github.io.wottrich.test.tools.BaseUnitTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import junit.framework.Assert.assertNull
import kotlinx.coroutines.flow.collect
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

class GetSelectedChecklistUseCaseTest : BaseUnitTest() {

    private lateinit var sut: GetSelectedChecklistUseCase

    private lateinit var checklistDao: ChecklistDao

    @Before
    fun sutUp() {
        checklistDao = mockk()
        sut = GetSelectedChecklistUseCase(checklistDao)
    }

    @Test
    fun `GIVEN use case requested WHEN has no checklist selected THEN must selected first and update it`() =
        runBlockingUnitTest {
            val checklistInserts = listOf(
                ChecklistWithTasks(
                    Checklist(checklistId = 0, name = "checklist 1"),
                    tasks = listOf(Task(taskId = 0, name = "task 1"))
                ),
                ChecklistWithTasks(
                    Checklist(checklistId = 1, name = "checklist 2"),
                    tasks = listOf(Task(taskId = 1, name = "task 2"))
                )
            )
            val expectedSelectedChecklist = checklistInserts.first()
            val localFlow = flow<List<ChecklistWithTasks>> {
                emit(listOf())
            }
            every { checklistDao.observeSelectedChecklistWithTasks(true) } returns localFlow
            coEvery { checklistDao.selectAllChecklistWithTasks() } returns checklistInserts
            coEvery { checklistDao.update(any()) } returns Unit

            sut().collect {
                assertNotNull(it)
                assertEquals(expectedSelectedChecklist, it)
            }

            verify(exactly = 1) { checklistDao.observeSelectedChecklistWithTasks(true) }
            coVerify { checklistDao.selectAllChecklistWithTasks() }
            coVerify { checklistDao.update(expectedSelectedChecklist.checklist) }
        }

    @Test
    fun `GIVEN use case requested WHEN has checklist selected THEN must returns selected checklist`() =
        runBlockingUnitTest {
            val checklistInserts = listOf(
                ChecklistWithTasks(
                    Checklist(checklistId = 0, name = "checklist 1", isSelected = true),
                    tasks = listOf(Task(taskId = 0, name = "task 1"))
                ),
                ChecklistWithTasks(
                    Checklist(checklistId = 1, name = "checklist 2"),
                    tasks = listOf(Task(taskId = 1, name = "task 2"))
                )
            )
            val expectedSelectedChecklist = checklistInserts.first()
            val localFlow = flow<List<ChecklistWithTasks>> {
                emit(checklistInserts)
            }
            every { checklistDao.observeSelectedChecklistWithTasks(true) } returns localFlow

            sut().collect {
                assertNotNull(it)
                assertEquals(expectedSelectedChecklist, it)
            }

            verify(exactly = 1) { checklistDao.observeSelectedChecklistWithTasks(true) }
            coVerify(inverse = true) { checklistDao.selectAllChecklistWithTasks() }
            coVerify(inverse = true) { checklistDao.update(any()) }
        }

    @Test
    fun `GIVEN use case requested WHEN checklists is empty THEN must returns null as selected checklist`() =
        runBlockingUnitTest {
            val checklistInserts = listOf<ChecklistWithTasks>()
            val localFlow = flow<List<ChecklistWithTasks>> {
                emit(checklistInserts)
            }
            every { checklistDao.observeSelectedChecklistWithTasks(true) } returns localFlow
            coEvery { checklistDao.selectAllChecklistWithTasks() } returns checklistInserts

            sut().collect {
                assertNull(it)
            }

            verify(exactly = 1) { checklistDao.observeSelectedChecklistWithTasks(true) }
            coVerify(exactly = 1) { checklistDao.selectAllChecklistWithTasks() }
            coVerify(inverse = true) { checklistDao.update(any()) }
        }


}
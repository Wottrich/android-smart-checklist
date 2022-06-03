package github.io.wottrich.checklist.domain.usecase

import github.io.wottrich.test.tools.BaseUnitTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import wottrich.github.io.datasource.dao.ChecklistDao
import wottrich.github.io.datasource.entity.Checklist

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 17/01/2022
 *
 * Copyright © 2022 AndroidSmartCheckList. All rights reserved.
 */

class UpdateSelectedChecklistUseCaseTest : BaseUnitTest() {

    private lateinit var sut: UpdateSelectedChecklistUseCase

    private lateinit var checklistDao: ChecklistDao

    @Before
    override fun setUp() {
        checklistDao = mockk()
        sut = UpdateSelectedChecklistUseCase(checklistDao)
    }

    @Test
    fun `GIVEN use case is requested WHEN has selected checklist THEN must change selected checklists`() = runBlockingUnitTest {
        val selectedChecklist = Checklist(
            checklistId = 0,
            name = "Checklist 0",
            isSelected = true
        )
        val nextSelectedChecklist = Checklist(
            checklistId = 1,
            name = "Checklist 1",
            isSelected = false
        )
        coEvery { checklistDao.selectSelectedChecklist(true) } returns selectedChecklist
        coEvery { checklistDao.updateChecklists(any()) } returns Unit

        sut(nextSelectedChecklist)

        coVerify(inverse = true) { checklistDao.update(any()) }
        coVerify(exactly = 1) {
            checklistDao.updateChecklists(listOf(selectedChecklist, nextSelectedChecklist))
        }

        assertTrue(nextSelectedChecklist.isSelected)
        assertFalse(selectedChecklist.isSelected)
    }

    @Test
    fun `GIVEN use case is requested WHEN has no selected checklist THEN must change selected checklists`() = runBlockingUnitTest {
        val selectedChecklist: Checklist? = null
        val nextSelectedChecklist = Checklist(
            checklistId = 1,
            name = "Checklist 1",
            isSelected = false
        )
        coEvery { checklistDao.selectSelectedChecklist(true) } returns selectedChecklist
        coEvery { checklistDao.update(any()) } returns Unit

        sut(nextSelectedChecklist)

        coVerify(exactly = 1) { checklistDao.update(nextSelectedChecklist) }
        coVerify(inverse = true) {
            checklistDao.updateChecklists(any())
        }

        assertTrue(nextSelectedChecklist.isSelected)
    }

}
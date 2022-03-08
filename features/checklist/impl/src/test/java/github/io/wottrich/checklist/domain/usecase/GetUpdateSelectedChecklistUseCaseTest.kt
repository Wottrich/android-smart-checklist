package github.io.wottrich.checklist.domain.usecase

import github.io.wottrich.test.tools.BaseUnitTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import wottrich.github.io.database.dao.ChecklistDao
import wottrich.github.io.database.entity.Checklist

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 17/01/2022
 *
 * Copyright © 2022 AndroidSmartCheckList. All rights reserved.
 */

class GetUpdateSelectedChecklistUseCaseTest : BaseUnitTest() {

    private lateinit var sut: GetUpdateSelectedChecklistUseCase

    private lateinit var checklistDao: ChecklistDao

    @Before
    fun setUp() {
        checklistDao = mockk()
        sut = GetUpdateSelectedChecklistUseCase(checklistDao)
    }

    @Test
    fun `GIVEN selected checklist WHEN use case is requested THEN must change selected checklists`() =
        runBlockingUnitTest {
            val mockedSelectedChecklist = Checklist(
                checklistId = 0,
                name = "Checklist0",
                isSelected = true
            )
            val nextSelectedChecklist = Checklist(
                1,
                name = "Checklist1",
                isSelected = false
            )
            coEvery { checklistDao.selectSelectedChecklist(true) } returns mockedSelectedChecklist
            coEvery { checklistDao.updateChecklists(any()) } returns Unit

            sut(nextSelectedChecklist)

            assertTrue(nextSelectedChecklist.isSelected)

            coVerify(exactly = 1) { checklistDao.updateChecklists(any()) }
        }

    @Test
    fun `GIVEN no selected checklist WHEN use case is requested THEN must select current checklist`() =
        runBlockingUnitTest {
            val mockedSelectedChecklist: Checklist? = null
            val nextSelectedChecklist = Checklist(
                1,
                name = "Checklist1",
                isSelected = false
            )
            coEvery { checklistDao.selectSelectedChecklist(true) } returns mockedSelectedChecklist
            coEvery { checklistDao.update(any()) } returns Unit

            sut(nextSelectedChecklist)

            assertTrue(nextSelectedChecklist.isSelected)

            coVerify(exactly = 1) { checklistDao.update(any()) }
        }

}
package github.io.wottrich.checklist.domain.usecase

import github.io.wottrich.test.tools.BaseUnitTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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

class GetDeleteChecklistUseCaseTest : BaseUnitTest() {

    private lateinit var sut: GetDeleteChecklistUseCase

    private lateinit var checklistDao: ChecklistDao

    @Before
    fun sutUp() {
        checklistDao = mockk()
        sut = GetDeleteChecklistUseCase(checklistDao)
    }

    @Test
    fun `WHEN use case is requested THEN must delete item`() = runBlockingUnitTest {
        val expectedChecklist = Checklist(checklistId = 0, name = "Checklist 0")
        coEvery { checklistDao.delete(any()) } returns Unit

        sut(expectedChecklist)

        coVerify(exactly = 1) { checklistDao.delete(expectedChecklist) }
    }

}
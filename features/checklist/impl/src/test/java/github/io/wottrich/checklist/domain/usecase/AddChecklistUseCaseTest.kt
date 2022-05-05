package github.io.wottrich.checklist.domain.usecase

import github.io.wottrich.test.tools.BaseUnitTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import wottrich.github.io.datasource.dao.ChecklistDao

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 17/01/2022
 *
 * Copyright © 2022 AndroidSmartCheckList. All rights reserved.
 */

class AddChecklistUseCaseTest : BaseUnitTest() {

    private lateinit var sut: AddChecklistUseCase

    private lateinit var checklistDao: ChecklistDao

    @Before
    override fun setUp() {
        checklistDao = mockk()
        sut = AddChecklistUseCase(checklistDao)
    }

    @Test
    fun `WHEN use case is requested THEN must insert new item in database`() = runBlockingUnitTest {
        val expectedItemId = 1L
        val expectedNameInsert = "Lucas"
        coEvery { checklistDao.insert(any()) } returns expectedItemId

        val itemId = sut(expectedNameInsert).getOrNull()

        assertEquals(expectedItemId, itemId)
        coVerify(exactly = 1) { checklistDao.insert(any()) }
    }

}
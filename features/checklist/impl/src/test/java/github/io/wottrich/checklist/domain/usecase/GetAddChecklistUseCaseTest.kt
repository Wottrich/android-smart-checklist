package github.io.wottrich.checklist.domain.usecase

import github.io.wottrich.test.tools.BaseUnitTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import wottrich.github.io.database.dao.ChecklistDao

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 17/01/2022
 *
 * Copyright © 2022 AndroidSmartCheckList. All rights reserved.
 */

class GetAddChecklistUseCaseTest : BaseUnitTest() {

    private lateinit var sut: GetAddChecklistUseCase

    private lateinit var checklistDao: ChecklistDao

    @Before
    fun setUp() {
        checklistDao = mockk()
        sut = GetAddChecklistUseCase(checklistDao)
    }

    @Test
    fun `WHEN use case is requested THEN must insert new item in database`() = runBlockingUnitTest {
        val expectedItemId = 1L
        val expectedNameInsert = "Lucas"
        coEvery { checklistDao.insert(any()) } returns expectedItemId

        val itemId = sut(expectedNameInsert)

        assertEquals(expectedItemId, itemId)
        coVerify(exactly = 1) { checklistDao.insert(any()) }
    }

}
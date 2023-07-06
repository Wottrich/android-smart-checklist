package github.io.wottrich.checklist.domain.usecase

import github.io.wottrich.test.tools.BaseUnitTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import wottrich.github.io.datasource.entity.NewChecklist
import wottrich.github.io.datasource.repository.ChecklistRepository

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 17/01/2022
 *
 * Copyright © 2022 AndroidSmartCheckList. All rights reserved.
 */

class UpdateSelectedChecklistUseCaseTest : BaseUnitTest() {

    private lateinit var sut: UpdateSelectedChecklistUseCaseImpl

    private lateinit var checklistRepository: ChecklistRepository

    @Before
    override fun setUp() {
        checklistRepository = mockk()
        sut = UpdateSelectedChecklistUseCaseImpl(checklistRepository)
    }

    @Test
    fun `GIVEN use case is requested WHEN has selected checklist THEN must change selected checklists`() =
        runBlockingUnitTest {
            val selectedChecklist = NewChecklist(
                uuid = "0",
                name = "Checklist 0",
                isSelected = true
            )
            val nextSelectedChecklist = NewChecklist(
                uuid = "1",
                name = "Checklist 1",
                isSelected = false
            )
            coEvery { checklistRepository.updateSelectedChecklist(any()) } returns Unit

            sut(nextSelectedChecklist.uuid)

            coVerify(exactly = 1) {
                checklistRepository.updateSelectedChecklist(nextSelectedChecklist.uuid)
            }

            assertTrue(nextSelectedChecklist.isSelected)
            assertFalse(selectedChecklist.isSelected)
        }

    @Test
    fun `GIVEN use case is requested WHEN has no selected checklist THEN must change selected checklists`() =
        runBlockingUnitTest {
            val nextSelectedChecklist = NewChecklist(
                uuid = "1",
                name = "Checklist 1",
                isSelected = false
            )
            coEvery { checklistRepository.updateSelectedChecklist(any()) } returns Unit

            sut(nextSelectedChecklist.uuid)

            coVerify(inverse = true) {
                checklistRepository.updateSelectedChecklist(any())
            }

            assertTrue(nextSelectedChecklist.isSelected)
        }
}
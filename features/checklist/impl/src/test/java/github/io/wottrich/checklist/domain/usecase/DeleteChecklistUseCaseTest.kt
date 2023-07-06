package github.io.wottrich.checklist.domain.usecase

import github.io.wottrich.test.tools.BaseUnitTest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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

class DeleteChecklistUseCaseTest : BaseUnitTest() {

    private lateinit var sut: DeleteChecklistUseCaseImpl

    private lateinit var checklistRepository: ChecklistRepository

    @Before
    fun sutUp() {
        checklistRepository = mockk()
        sut = DeleteChecklistUseCaseImpl(checklistRepository)
    }

    @Test
    fun `WHEN use case is requested THEN must delete item`() = runBlockingUnitTest {
        val expectedChecklist = NewChecklist(uuid = "0", name = "Checklist 0")
        coEvery { checklistRepository.deleteChecklistByUuid(any()) } returns Unit

        sut(expectedChecklist.uuid)

        coVerify(exactly = 1) { checklistRepository.deleteChecklistByUuid(expectedChecklist.uuid) }
    }

}
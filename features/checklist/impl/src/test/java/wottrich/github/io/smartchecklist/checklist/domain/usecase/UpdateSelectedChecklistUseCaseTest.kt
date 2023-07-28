package wottrich.github.io.smartchecklist.checklist.domain.usecase

import github.io.wottrich.test.tools.BaseUnitTest
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test
import wottrich.github.io.smartchecklist.checklist.domain.usecase.UpdateSelectedChecklistUseCaseImpl
import wottrich.github.io.smartchecklist.datasource.repository.ChecklistRepository
import kotlin.test.assertTrue

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 17/01/2022
 *
 * Copyright © 2022 AndroidSmartCheckList. All rights reserved.
 */

@OptIn(ExperimentalCoroutinesApi::class)
class UpdateSelectedChecklistUseCaseTest : BaseUnitTest() {

    private lateinit var sut: UpdateSelectedChecklistUseCaseImpl

    private lateinit var checklistRepository: ChecklistRepository

    private val dummyChecklistUuid = "123"

    @Before
    override fun setUp() {
        checklistRepository = mockk()
        sut = UpdateSelectedChecklistUseCaseImpl(checklistRepository)
    }

    @Test
    fun `GIVEN update checklist finished with success WHEN usecase is requested THEN must returns success`() =
        runBlockingUnitTest {
            coEvery { checklistRepository.updateSelectedChecklist(any()) } returns Unit
            val result = sut(dummyChecklistUuid)
            assertTrue(result.isSuccess)
        }

    @Test
    fun `GIVEN update checklist throw error WHEN usecase is requested THEN must returns failure`() =
        runBlockingUnitTest {
            coEvery { checklistRepository.updateSelectedChecklist(any()) } throws RuntimeException()
            val result = sut(dummyChecklistUuid)
            assertTrue(result.isFailure)
        }
}
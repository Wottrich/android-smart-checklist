package github.io.wottrich.checklist.domain.usecase

import github.io.wottrich.test.tools.BaseUnitTest
import io.mockk.mockk
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

class GetUpdateSelectedChecklistUseCaseTest : BaseUnitTest() {

    private lateinit var sut: GetUpdateSelectedChecklistUseCase

    private lateinit var checklistDao: ChecklistDao

    @Before
    fun setUp() {
        checklistDao = mockk()
        sut = GetUpdateSelectedChecklistUseCase(checklistDao)
    }

    @Test
    fun `GIVEN use case is requested WHEN has selected checklist THEN must change selected checklists`() {}

}
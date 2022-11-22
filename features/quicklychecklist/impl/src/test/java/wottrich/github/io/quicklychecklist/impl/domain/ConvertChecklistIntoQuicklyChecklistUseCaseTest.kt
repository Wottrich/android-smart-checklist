package wottrich.github.io.quicklychecklist.impl.domain

import org.junit.Test
import wottrich.github.io.datasource.entity.NewTask
import wottrich.github.io.datasource.entity.QuicklyChecklist

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 21/11/2022
 *
 * Copyright © 2022 AndroidSmartCheckList. All rights reserved.
 */

class ConvertChecklistIntoQuicklyChecklistUseCaseTest {

    private lateinit var sut: ConvertChecklistIntoQuicklyChecklistUseCase

    private val dummyNewTask = NewTask("test_task_uuid", "test_checklist_uuid", name = "newTask")

    @Test
    fun `GIVEN has new checklist with new tasks object WHEN use case is called THEN must returns quickly checklist object`() {
        val expectedQuicklyChecklist = QuicklyChecklist(
            "test_checklist_uuid",
            tasks = listOf(
                NewTask("test_task_uuid", "test_checklist_uuid", name = "newTask")
            )
        )
    }

    // TODO finish do all tests here
}
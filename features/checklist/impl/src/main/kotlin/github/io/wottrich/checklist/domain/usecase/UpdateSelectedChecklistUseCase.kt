package github.io.wottrich.checklist.domain.usecase

import wottrich.github.io.datasource.dao.ChecklistDao
import wottrich.github.io.datasource.entity.Checklist

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 06/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

class UpdateSelectedChecklistUseCase(private val checklistDao: ChecklistDao) {
    suspend operator fun invoke(checklist: Checklist) {
        val currentSelectedChecklist = checklistDao.selectSelectedChecklist(true)
        currentSelectedChecklist?.isSelected = false
        checklist.isSelected = true
        if (currentSelectedChecklist == null) {
            checklistDao.update(checklist)
        } else {
            checklistDao.updateChecklists(listOf(currentSelectedChecklist, checklist))
        }
    }
}
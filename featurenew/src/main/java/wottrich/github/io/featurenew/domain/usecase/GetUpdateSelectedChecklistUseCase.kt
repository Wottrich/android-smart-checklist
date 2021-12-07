package wottrich.github.io.featurenew.domain.usecase

import wottrich.github.io.database.dao.ChecklistDao
import wottrich.github.io.database.entity.Checklist

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 06/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

class GetUpdateSelectedChecklistUseCase(
    private val checklistDao: ChecklistDao
) {

    suspend operator fun invoke(checklist: Checklist) {
        val currentSelectedChecklist = checklistDao.selectSelectedChecklist(true)
        currentSelectedChecklist?.isSelected = false
        currentSelectedChecklist?.let {
            checklistDao.update(currentSelectedChecklist)
        }
        checklist.isSelected = true
        checklistDao.update(checklist)
    }

}
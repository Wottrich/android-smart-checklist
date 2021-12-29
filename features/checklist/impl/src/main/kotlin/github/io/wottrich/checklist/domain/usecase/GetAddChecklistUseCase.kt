package github.io.wottrich.checklist.domain.usecase

import wottrich.github.io.database.dao.ChecklistDao
import wottrich.github.io.database.entity.Checklist

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 27/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

class GetAddChecklistUseCase(private val checklistDao: ChecklistDao) {
    suspend operator fun invoke(checklistName: String): Long? {
        return checklistDao.insert(Checklist(name = checklistName))
    }
}
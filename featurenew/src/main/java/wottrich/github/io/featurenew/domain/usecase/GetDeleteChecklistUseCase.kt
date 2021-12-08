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

class GetDeleteChecklistUseCase(private val checklistDao: ChecklistDao) {

    suspend operator fun invoke(checklist: Checklist) {
        checklistDao.delete(checklist)
    }

}
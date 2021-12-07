package wottrich.github.io.featurenew.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import wottrich.github.io.database.dao.ChecklistDao
import wottrich.github.io.database.entity.ChecklistWithTasks

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 06/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

class GetSelectedChecklistUseCase(private val checklistDao: ChecklistDao) {

    suspend operator fun invoke(): Flow<ChecklistWithTasks?> {
        return flow {
            checklistDao.observeSelectedChecklistWithTasks(true).collect {
                var selectedChecklist = it
                if (selectedChecklist == null) {
                    selectedChecklist = checklistDao.selectAllChecklistWithTasks().firstOrNull()
                    if (selectedChecklist != null) {
                        val checklist = selectedChecklist.checklist
                        checklist.isSelected = true
                        checklistDao.update(checklist)
                    }
                }
                emit(selectedChecklist)
            }
        }
    }

}
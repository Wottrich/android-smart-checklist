package github.io.wottrich.checklist.domain.usecase

import wottrich.github.io.datasource.dao.ChecklistDao
import wottrich.github.io.datasource.entity.Checklist
import wottrich.github.io.tools.base.KotlinResultUseCase
import wottrich.github.io.tools.base.Result
import wottrich.github.io.tools.base.UseCase
import wottrich.github.io.tools.base.failureEmptyResult
import wottrich.github.io.tools.base.successEmptyResult

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 06/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

class UpdateSelectedChecklistUseCase(private val checklistDao: ChecklistDao) :
    KotlinResultUseCase<Checklist, UseCase.Empty>() {
    override suspend fun execute(params: Checklist): Result<UseCase.Empty> {
        return try {
            val currentSelectedChecklist = checklistDao.selectSelectedChecklist(true)
            currentSelectedChecklist?.isSelected = false
            params.isSelected = true
            if (currentSelectedChecklist == null) {
                checklistDao.update(params)
            } else {
                checklistDao.updateChecklists(listOf(currentSelectedChecklist, params))
            }
            successEmptyResult()
        } catch (ex: Exception) {
            failureEmptyResult(ex)
        }
    }
}
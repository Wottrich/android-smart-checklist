package github.io.wottrich.checklist.domain.usecase

import github.io.wottrich.coroutines.KotlinResultUseCase
import github.io.wottrich.coroutines.UseCase
import github.io.wottrich.coroutines.base.Result
import github.io.wottrich.coroutines.failureEmptyResult
import github.io.wottrich.coroutines.successEmptyResult
import wottrich.github.io.datasource.dao.ChecklistDao
import wottrich.github.io.datasource.entity.NewChecklist

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 06/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

class UpdateSelectedChecklistUseCase(private val checklistDao: ChecklistDao) :
    KotlinResultUseCase<NewChecklist, UseCase.Empty>() {
    override suspend fun execute(params: NewChecklist): Result<UseCase.Empty> {
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
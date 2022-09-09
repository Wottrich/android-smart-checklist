package github.io.wottrich.checklist.domain.usecase

import wottrich.github.io.datasource.dao.ChecklistDao
import wottrich.github.io.datasource.entity.NewChecklist
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

class DeleteChecklistUseCase(private val checklistDao: ChecklistDao) :
    KotlinResultUseCase<NewChecklist, UseCase.Empty>() {
    override suspend fun execute(params: NewChecklist): Result<UseCase.Empty> {
        return try {
            checklistDao.delete(params)
            successEmptyResult()
        } catch (ex: Exception) {
            failureEmptyResult(ex)
        }
    }
}
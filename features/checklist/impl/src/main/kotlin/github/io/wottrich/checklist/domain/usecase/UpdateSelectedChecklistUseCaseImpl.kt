package github.io.wottrich.checklist.domain.usecase

import github.io.wottrich.checklist.domain.UpdateSelectedChecklistUseCase
import github.io.wottrich.coroutines.UseCase
import github.io.wottrich.coroutines.base.Result
import github.io.wottrich.coroutines.failureEmptyResult
import github.io.wottrich.coroutines.successEmptyResult
import wottrich.github.io.smartchecklist.datasource.repository.ChecklistRepository

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 06/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

class UpdateSelectedChecklistUseCaseImpl(
    private val checklistRepository: ChecklistRepository
) : UpdateSelectedChecklistUseCase() {
    override suspend fun execute(params: String): Result<UseCase.Empty> {
        return try {
            checklistRepository.updateSelectedChecklist(params)
            successEmptyResult()
        } catch (ex: Exception) {
            failureEmptyResult(ex)
        }
    }
}
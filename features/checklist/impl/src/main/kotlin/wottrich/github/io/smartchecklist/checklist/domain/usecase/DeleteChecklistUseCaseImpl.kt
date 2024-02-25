package wottrich.github.io.smartchecklist.checklist.domain.usecase

import wottrich.github.io.smartchecklist.checklist.data.repository.ChecklistRepository
import wottrich.github.io.smartchecklist.checklist.domain.DeleteChecklistUseCase
import wottrich.github.io.smartchecklist.coroutines.UseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.coroutines.failureEmptyResult
import wottrich.github.io.smartchecklist.coroutines.successEmptyResult

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 06/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

internal class DeleteChecklistUseCaseImpl(
    private val checklistRepository: ChecklistRepository
) : DeleteChecklistUseCase() {
    override suspend fun execute(params: String): Result<UseCase.Empty> {
        return try {
            checklistRepository.deleteChecklistByUuid(params)
            successEmptyResult()
        } catch (ex: Exception) {
            failureEmptyResult(ex)
        }
    }
}
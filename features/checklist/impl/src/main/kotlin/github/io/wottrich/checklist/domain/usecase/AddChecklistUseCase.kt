package github.io.wottrich.checklist.domain.usecase

import wottrich.github.io.datasource.dao.ChecklistDao
import wottrich.github.io.datasource.entity.NewChecklist
import wottrich.github.io.tools.base.KotlinResultUseCase
import wottrich.github.io.tools.base.Result

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 27/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */
@Deprecated("Use AddNewChecklistUseCase")
class AddChecklistUseCase(private val checklistDao: ChecklistDao) :
    KotlinResultUseCase<String, Long?>() {
    override suspend fun execute(params: String): Result<Long?> {
        return try {
            Result.success(checklistDao.insert(NewChecklist(name = params)))
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}
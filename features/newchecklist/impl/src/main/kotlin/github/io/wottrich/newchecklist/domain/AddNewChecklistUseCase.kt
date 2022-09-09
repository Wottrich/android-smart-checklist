package github.io.wottrich.newchecklist.domain

import wottrich.github.io.datasource.dao.ChecklistDao
import wottrich.github.io.datasource.entity.NewChecklist
import wottrich.github.io.tools.base.KotlinResultUseCase
import wottrich.github.io.tools.base.Result

class AddNewChecklistUseCase(private val checklistDao: ChecklistDao) :
    KotlinResultUseCase<NewChecklist, Long?>() {
    override suspend fun execute(params: NewChecklist): Result<Long?> {
        return try {
            Result.success(checklistDao.insert(params))
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}
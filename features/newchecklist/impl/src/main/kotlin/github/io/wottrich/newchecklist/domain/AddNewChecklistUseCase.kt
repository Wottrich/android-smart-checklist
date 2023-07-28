package github.io.wottrich.newchecklist.domain

import wottrich.github.io.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.datasource.dao.ChecklistDao
import wottrich.github.io.smartchecklist.datasource.entity.NewChecklist
import wottrich.github.io.coroutines.base.Result

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
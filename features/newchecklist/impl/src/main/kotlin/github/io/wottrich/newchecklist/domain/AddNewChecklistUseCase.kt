package github.io.wottrich.newchecklist.domain

import wottrich.github.io.datasource.dao.ChecklistDao
import wottrich.github.io.datasource.entity.Checklist
import wottrich.github.io.tools.base.KotlinResultUseCase

class AddNewChecklistUseCase(private val checklistDao: ChecklistDao) : KotlinResultUseCase<Checklist, Long?>() {
    override suspend fun execute(params: Checklist): Result<Long?> {
        return try {
            Result.success(checklistDao.insert(params))
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }
}
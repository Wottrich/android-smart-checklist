package github.io.wottrich.newchecklist.domain

import wottrich.github.io.datasource.dao.ChecklistDao
import wottrich.github.io.datasource.entity.Checklist

class AddNewChecklistUseCase(private val checklistDao: ChecklistDao) {
    suspend operator fun invoke(checklist: Checklist): Long? {
        return checklistDao.insert(checklist)
    }
}
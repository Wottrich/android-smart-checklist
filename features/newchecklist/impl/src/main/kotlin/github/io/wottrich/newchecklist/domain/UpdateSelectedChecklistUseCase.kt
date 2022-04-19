package github.io.wottrich.newchecklist.domain

import wottrich.github.io.datasource.dao.ChecklistDao
import wottrich.github.io.datasource.entity.Checklist

class UpdateSelectedChecklistUseCase(private val checklistDao: ChecklistDao) {
    suspend operator fun invoke(checklist: Checklist) {
        val currentSelectedChecklist = checklistDao.selectSelectedChecklist(true)
        currentSelectedChecklist?.isSelected = false
        checklist.isSelected = true
        if (currentSelectedChecklist == null) {
            checklistDao.update(checklist)
        } else {
            checklistDao.updateChecklists(listOf(currentSelectedChecklist, checklist))
        }
    }
}
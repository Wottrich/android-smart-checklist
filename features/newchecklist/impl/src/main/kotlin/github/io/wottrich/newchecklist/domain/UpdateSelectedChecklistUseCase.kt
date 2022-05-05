package github.io.wottrich.newchecklist.domain

import wottrich.github.io.datasource.dao.ChecklistDao
import wottrich.github.io.datasource.entity.Checklist
import wottrich.github.io.tools.base.KotlinResultUseCase
import wottrich.github.io.tools.base.Result
import wottrich.github.io.tools.base.UseCase
import wottrich.github.io.tools.base.successEmptyResult

class UpdateSelectedChecklistUseCase(private val checklistDao: ChecklistDao) :
    KotlinResultUseCase<Checklist, UseCase.Empty>() {
    override suspend fun execute(params: Checklist): Result<UseCase.Empty> {
        val currentSelectedChecklist = checklistDao.selectSelectedChecklist(true)
        currentSelectedChecklist?.isSelected = false
        params.isSelected = true
        if (currentSelectedChecklist == null) {
            checklistDao.update(params)
        } else {
            checklistDao.updateChecklists(listOf(currentSelectedChecklist, params))
        }
        return successEmptyResult()
    }
}
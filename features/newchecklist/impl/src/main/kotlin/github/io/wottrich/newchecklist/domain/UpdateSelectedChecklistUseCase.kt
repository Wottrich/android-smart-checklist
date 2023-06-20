package github.io.wottrich.newchecklist.domain

import github.io.wottrich.coroutines.KotlinResultUseCase
import github.io.wottrich.coroutines.UseCase
import github.io.wottrich.coroutines.base.Result
import github.io.wottrich.coroutines.successEmptyResult
import wottrich.github.io.datasource.dao.ChecklistDao
import wottrich.github.io.datasource.entity.NewChecklist

class UpdateSelectedChecklistUseCase(private val checklistDao: ChecklistDao) :
    KotlinResultUseCase<NewChecklist, UseCase.Empty>() {
    override suspend fun execute(params: NewChecklist): Result<UseCase.Empty> {
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
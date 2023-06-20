package github.io.wottrich.checklist.domain.usecase

import github.io.wottrich.coroutines.FlowableUseCase
import github.io.wottrich.coroutines.UseCase
import github.io.wottrich.coroutines.base.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import wottrich.github.io.datasource.dao.ChecklistDao
import wottrich.github.io.datasource.entity.NewChecklistWithNewTasks

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 06/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

class GetSelectedChecklistUseCase(private val checklistDao: ChecklistDao) :
    FlowableUseCase<UseCase.None, NewChecklistWithNewTasks?>() {
    override suspend fun execute(params: UseCase.None): Flow<Result<NewChecklistWithNewTasks?>> {
        return flow {
            checklistDao.observeSelectedChecklistWithTasks()
                .distinctUntilChanged()
                .collect(
                    FlowCollector<List<NewChecklistWithNewTasks>> { value ->
                        var selectedChecklist = value.firstOrNull()
                        if (selectedChecklist == null) {
                            selectedChecklist =
                                checklistDao.selectAllChecklistWithTasks().firstOrNull()
                            if (selectedChecklist != null) {
                                val checklist = selectedChecklist.newChecklist
                                checklist.isSelected = true
                                checklistDao.update(checklist)
                            }
                        }
                        emit(Result.success(selectedChecklist))
                    }
                )
        }
    }
}
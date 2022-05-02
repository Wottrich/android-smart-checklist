package github.io.wottrich.checklist.domain.usecase

import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import wottrich.github.io.datasource.dao.ChecklistDao
import wottrich.github.io.datasource.entity.ChecklistWithTasks
import wottrich.github.io.tools.base.FlowableUseCase
import wottrich.github.io.tools.base.UseCase
import wottrich.github.io.tools.base.UseCase.None

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 06/12/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

class GetSelectedChecklistUseCase(private val checklistDao: ChecklistDao) : FlowableUseCase<UseCase.None, ChecklistWithTasks?>() {
    @OptIn(InternalCoroutinesApi::class)
    override suspend fun execute(params: None): Flow<Result<ChecklistWithTasks?>> {
        return flow {
            checklistDao.observeSelectedChecklistWithTasks(true).collect(
                FlowCollector<List<ChecklistWithTasks>> { value ->
                    var selectedChecklist = value.firstOrNull()
                    if (selectedChecklist == null) {
                        selectedChecklist = checklistDao.selectAllChecklistWithTasks().firstOrNull()
                        if (selectedChecklist != null) {
                            val checklist = selectedChecklist.checklist
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
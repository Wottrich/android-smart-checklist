package wottrich.github.io.quicklychecklist.impl.domain

import com.google.gson.Gson
import github.io.wottrich.coroutines.KotlinResultUseCase
import github.io.wottrich.coroutines.base.Result
import wottrich.github.io.datasource.entity.NewChecklistWithNewTasks
import wottrich.github.io.datasource.entity.NewTask
import wottrich.github.io.datasource.entity.QuicklyChecklist
import wottrich.github.io.datasource.entity.QuicklyTask

class ConvertChecklistIntoQuicklyChecklistUseCase :
    KotlinResultUseCase<NewChecklistWithNewTasks, String>() {
    override suspend fun execute(params: NewChecklistWithNewTasks): Result<String> {
        val quicklyChecklist = createQuicklyChecklist(params)
        return try {
            Result.success(Gson().toJson(quicklyChecklist))
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    private fun createQuicklyChecklist(checklistWithTasks: NewChecklistWithNewTasks): QuicklyChecklist {
        return QuicklyChecklist(
            checklistUuid = checklistWithTasks.newChecklist.uuid,
            tasks = checklistWithTasks.newTasks.toQuicklyTaskList()
        )
    }

    private fun List<NewTask>.toQuicklyTaskList() =
        this.map { QuicklyTask(it.name, it.isCompleted) }
}
package wottrich.github.io.quicklychecklist.impl.domain

import com.google.gson.Gson
import github.io.wottrich.coroutines.KotlinResultUseCase
import github.io.wottrich.coroutines.base.Result
import wottrich.github.io.datasource.entity.NewChecklistWithNewTasks
import wottrich.github.io.datasource.entity.NewTask
import wottrich.github.io.datasource.entity.QuicklyChecklist
import wottrich.github.io.datasource.entity.QuicklyTask
import wottrich.github.io.datasource.repository.ChecklistRepository

class ConvertChecklistIntoQuicklyChecklistUseCase(
    private val checklistRepository: ChecklistRepository
) : KotlinResultUseCase<String, String>() {
    override suspend fun execute(params: String): Result<String> {
        val checklist = checklistRepository.getChecklistWithTasksByUuid(params)
        val quicklyChecklist = createQuicklyChecklist(checklist)
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
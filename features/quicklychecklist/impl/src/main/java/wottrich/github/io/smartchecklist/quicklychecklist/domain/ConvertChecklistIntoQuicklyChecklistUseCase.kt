package wottrich.github.io.smartchecklist.quicklychecklist.domain

import com.google.gson.Gson
import wottrich.github.io.smartchecklist.coroutines.KotlinResultUseCase
import wottrich.github.io.smartchecklist.coroutines.base.Result
import wottrich.github.io.smartchecklist.datasource.data.model.ChecklistWithTasks
import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.datasource.data.model.QuicklyChecklist
import wottrich.github.io.smartchecklist.datasource.data.model.QuicklyTask
import wottrich.github.io.smartchecklist.checklist.data.repository.ChecklistRepository

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

    private fun createQuicklyChecklist(checklistWithTasks: ChecklistWithTasks): QuicklyChecklist {
        return QuicklyChecklist(
            checklistUuid = checklistWithTasks.checklist.uuid,
            tasks = checklistWithTasks.tasks.toQuicklyTaskList()
        )
    }

    private fun List<Task>.toQuicklyTaskList() =
        this.map { QuicklyTask(it.name, it.isCompleted) }
}
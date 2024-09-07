package wottrich.github.io.smartchecklist.quicklychecklist.domain.data

import wottrich.github.io.smartchecklist.datasource.data.model.Task
import wottrich.github.io.smartchecklist.uuid.UuidGenerator

data class QuicklyChecklist(
    val checklistUuid: String = "",
    val tasks: List<QuicklyTask> = listOf()
) {
    fun getConvertedTasks() = tasks.map {
        Task(
            uuid = UuidGenerator.getRandomUuid(),
            parentUuid = checklistUuid,
            name = it.name,
            isCompleted = it.isCompleted
        )
    }
}

data class QuicklyTask(
    val name: String,
    val isCompleted: Boolean
)

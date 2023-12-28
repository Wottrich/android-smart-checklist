package wottrich.github.io.smartchecklist.datasource.entity

import androidx.room.Embedded
import androidx.room.Relation
import wottrich.github.io.smartchecklist.datasource.data.model.ChecklistWithNewTasksContract

data class ChecklistWithTasksDTO(
    @Embedded override val checklist: ChecklistDTO,
    @Relation(
        parentColumn = "uuid",
        entityColumn = "parent_uuid"
    )
    override val tasks: List<TaskDTO>
): ChecklistWithNewTasksContract {
    override fun toString(): String {
        val string = StringBuilder()

        string.append("Checklist: ")
        string.append(checklist.name)
        string.appendLine()

        tasks.forEach {
            val isCompleted = if(it.isCompleted) "✓ - " else "✗ - "
            string.append(isCompleted)
            string.appendLine(it.name)
        }

        return string.toString()
    }
}
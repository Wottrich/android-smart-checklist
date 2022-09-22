package wottrich.github.io.datasource.entity

import androidx.room.Embedded
import androidx.room.Relation

data class NewChecklistWithNewTasks(
    @Embedded val newChecklist: NewChecklist,
    @Relation(
        parentColumn = "uuid",
        entityColumn = "parent_uuid"
    )
    val newTasks: List<NewTask>
) {
    override fun toString(): String {
        val string = StringBuilder()

        string.append("Checklist: ")
        string.append(newChecklist.name)
        string.appendLine()

        newTasks.forEach {
            val isCompleted = if(it.isCompleted) "✓ - " else "✗ - "
            string.append(isCompleted)
            string.appendLine(it.name)
        }

        return string.toString()
    }
}
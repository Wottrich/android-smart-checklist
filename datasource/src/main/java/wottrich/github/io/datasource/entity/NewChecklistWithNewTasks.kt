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
}
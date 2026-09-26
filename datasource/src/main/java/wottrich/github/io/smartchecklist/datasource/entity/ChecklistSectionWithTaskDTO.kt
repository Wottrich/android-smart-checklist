package wottrich.github.io.smartchecklist.datasource.entity

import androidx.room.Embedded
import androidx.room.Relation
import wottrich.github.io.smartchecklist.datasource.data.model.ChecklistSectionWithTaskContract

data class ChecklistSectionWithTaskDTO(
    @Embedded override val checklistSection: ChecklistDTO,
    @Relation(
        parentColumn = "uuid",
        entityColumn = "parent_uuid"
    )
    override val tasks: List<TaskDTO>
) : ChecklistSectionWithTaskContract
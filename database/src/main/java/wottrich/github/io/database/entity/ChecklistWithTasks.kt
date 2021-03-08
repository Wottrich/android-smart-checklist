package wottrich.github.io.database.entity

import androidx.room.Embedded
import androidx.room.Relation

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 07/03/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */
 
data class ChecklistWithTasks(
    @Embedded val checklist: Checklist,
    @Relation(
        parentColumn = "checklistId",
        entityColumn = "checklistId"
    )
    val tasks: List<Task>
)
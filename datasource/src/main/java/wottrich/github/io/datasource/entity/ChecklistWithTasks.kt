package wottrich.github.io.datasource.entity

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
) {
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
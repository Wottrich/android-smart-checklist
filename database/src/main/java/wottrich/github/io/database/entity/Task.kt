package wottrich.github.io.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 13/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */

object TaskType {
    const val Normal = 0
}

@Entity(tableName = "task")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val taskId: Long? = null,
    var checklistId: Long? = null,
    var name: String,
    var isCompleted: Boolean = false,
    var taskType: Int = TaskType.Normal,
    val dateCreated: Calendar = Calendar.getInstance()
)
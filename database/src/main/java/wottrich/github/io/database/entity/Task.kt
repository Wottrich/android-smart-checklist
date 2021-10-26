package wottrich.github.io.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import java.util.Calendar

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

@Entity(
    tableName = "task",
    foreignKeys = [
        ForeignKey(
            onDelete = CASCADE,
            entity = Checklist::class,
            parentColumns = arrayOf("checklistId"),
            childColumns = arrayOf("checklistId")
        )
    ]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    var taskId: Long? = null,
    var checklistId: Long? = null,
    var name: String,
    var isCompleted: Boolean = false,
    var taskType: Int = TaskType.Normal,
    val dateCreated: Calendar = Calendar.getInstance()
)
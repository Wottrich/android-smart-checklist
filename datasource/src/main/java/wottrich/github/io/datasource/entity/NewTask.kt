package wottrich.github.io.datasource.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Calendar
import wottrich.github.io.tools.base.UuidGenerator


@Entity(
    tableName = "new_task",
    foreignKeys = [
        ForeignKey(
            onDelete = ForeignKey.CASCADE,
            entity = NewChecklist::class,
            parentColumns = arrayOf("uuid"),
            childColumns = arrayOf("parent_uuid")
        )
    ]
)
data class NewTask(
    @PrimaryKey
    var uuid: String = UuidGenerator.getRandomUuid(),
    @ColumnInfo(name = "parent_uuid")
    var parentUuid: String,
    var name: String,
    @ColumnInfo(name = "is_completed")
    var isCompleted: Boolean = false,
    @ColumnInfo(name = "date_created")
    val dateCreated: Calendar = Calendar.getInstance()
)

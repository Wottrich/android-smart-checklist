package wottrich.github.io.smartchecklist.datasource.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.Calendar
import wottrich.github.io.smartchecklist.uuid.UuidGenerator

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
    override val uuid: String = UuidGenerator.getRandomUuid(),
    @ColumnInfo(name = "parent_uuid")
    override val parentUuid: String,
    override val name: String,
    @ColumnInfo(name = "is_completed")
    override val isCompleted: Boolean = false,
    @ColumnInfo(name = "date_created")
    val dateCreated: Calendar = Calendar.getInstance()
): TaskContract

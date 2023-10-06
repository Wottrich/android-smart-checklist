package wottrich.github.io.smartchecklist.datasource.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Calendar
import wottrich.github.io.smartchecklist.uuid.UuidGenerator

@Entity(tableName = "new_checklist")
data class NewChecklist(
    @ColumnInfo(name = "uuid")
    @PrimaryKey
    override val uuid: String = UuidGenerator.getRandomUuid(),
    @ColumnInfo(name = "name")
    override val name: String,
    @ColumnInfo(name = "is_selected", defaultValue = "0")
    override val isSelected: Boolean = false,
    @ColumnInfo(name = "created_date")
    val createdDate: Calendar = Calendar.getInstance(),
    @ColumnInfo(name = "last_update")
    var lastUpdate: Calendar = Calendar.getInstance(),
) : ChecklistContract
package wottrich.github.io.database.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import wottrich.github.io.tools.extensions.validAndFormatDate
import java.util.*
import kotlinx.android.parcel.Parcelize

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 12/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */

@Entity(tableName = "checklist")
@Parcelize
data class Checklist(
    @PrimaryKey(autoGenerate = true)
    val checklistId: Long? = null,
    val name: String,
    @ColumnInfo(name = "created_date")
    val createdDate: Calendar = Calendar.getInstance(),
    @ColumnInfo(name = "last_update")
    var lastUpdate: Calendar = Calendar.getInstance()
) : Parcelable {

    @Ignore
    val createdDateFormatted: String? = createdDate.timeInMillis.validAndFormatDate()

    @Ignore
    val latestUpdateFormatted: String? = lastUpdate.timeInMillis.validAndFormatDate()

}
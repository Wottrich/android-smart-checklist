package wottrich.github.io.smartchecklist.datasource.converter

import androidx.room.TypeConverter
import java.util.Calendar

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 13/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */

open class Converters {
    @TypeConverter
    open fun calendarToDateStamp(calendar: Calendar): Long = calendar.timeInMillis

    @TypeConverter
    open fun dateStampToCalendar(value: Long): Calendar =
        Calendar.getInstance().apply { timeInMillis = value }
}
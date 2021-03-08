package wottrich.github.io.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update
import wottrich.github.io.database.entity.Task

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 07/03/2021
 *
 * Copyright © 2021 AndroidSmartCheckList. All rights reserved.
 *
 */

@Dao
interface TaskDao {

    @Insert
    suspend fun insert(task: Task): Long?

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

}
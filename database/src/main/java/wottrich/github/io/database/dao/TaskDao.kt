package wottrich.github.io.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
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

    @Transaction
    @Query("SELECT * FROM task WHERE checklistId=:checklistId")
    fun observeTasksUpdate(checklistId: String): Flow<List<Task>>

    @Transaction
    @Query("SELECT * FROM task WHERE checklistId=:checklistId")
    suspend fun getTasks(checklistId: String): List<Task>

    @Insert
    suspend fun insert(task: Task): Long?

    @Update
    suspend fun update(task: Task)

    @Update
    suspend fun updateTasks(tasks: List<Task>)

    @Delete
    suspend fun delete(task: Task)

}
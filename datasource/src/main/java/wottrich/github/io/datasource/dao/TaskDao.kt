package wottrich.github.io.datasource.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import wottrich.github.io.datasource.entity.NewTask

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
    @Query("SELECT * FROM new_task WHERE parent_uuid=:parentUuid")
    fun observeTasksUpdate(parentUuid: String): Flow<List<NewTask>>

    @Transaction
    @Query("SELECT * FROM new_task WHERE parent_uuid=:parentUuid")
    suspend fun getTasks(parentUuid: String): List<NewTask>

    @Transaction
    @Insert(entity = NewTask::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: NewTask): Long?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMany(tasks: List<NewTask>)

    @Update
    suspend fun update(task: NewTask)

    @Update
    suspend fun updateTasks(tasks: List<NewTask>)

    @Delete
    suspend fun delete(task: NewTask)
}
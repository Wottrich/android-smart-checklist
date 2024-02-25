package wottrich.github.io.smartchecklist.datasource.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import wottrich.github.io.smartchecklist.datasource.entity.TaskDTO

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
    fun observeTasksUpdate(parentUuid: String): Flow<List<TaskDTO>>

    @Transaction
    @Query("SELECT * FROM new_task WHERE parent_uuid=:parentUuid")
    suspend fun getTasks(parentUuid: String): List<TaskDTO>

    @Transaction
    @Insert(entity = TaskDTO::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(task: TaskDTO): Long?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMany(tasks: List<TaskDTO>)

    @Update
    suspend fun update(task: TaskDTO)

    @Update
    suspend fun updateTasks(tasks: List<TaskDTO>)

    @Delete
    suspend fun delete(task: TaskDTO)
}
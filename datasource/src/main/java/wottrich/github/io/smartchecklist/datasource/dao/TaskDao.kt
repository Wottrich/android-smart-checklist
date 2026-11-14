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

    /** Null when missing — used by the server-sync engine (pull apply / payload reads). */
    @Query("SELECT * FROM new_task WHERE uuid=:uuid")
    suspend fun findTask(uuid: String): TaskDTO?

    /** Direct delete by uuid; missing row is a no-op (idempotent for sync applies). */
    @Query("DELETE FROM new_task WHERE uuid=:uuid")
    suspend fun deleteByUuid(uuid: String)

    @Update
    suspend fun update(task: TaskDTO)

    @Delete
    suspend fun delete(task: TaskDTO)
}
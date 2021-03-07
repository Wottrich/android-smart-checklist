package wottrich.github.io.database.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import wottrich.github.io.database.entity.Checklist

/**
 * @author Wottrich
 * @author wottrich78@gmail.com
 * @since 13/09/2020
 *
 * Copyright © 2020 AndroidSmartCheckList. All rights reserved.
 *
 */

@Dao
interface ChecklistDao {

    @Transaction
    @Query("SELECT * FROM checklist")
    fun selectAllFromChecklist() : Flow<List<Checklist>>

    @Transaction
    @Query("SELECT * FROM checklist WHERE checklistId=:checklistId")
    fun selectAllFromChecklistWhereChecklistIdIs(checklistId: Long): Flow<Checklist>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(checklist: Checklist): Long?

    @Update
    suspend fun update(checklist: Checklist)

    @Delete
    suspend fun delete(checklist: Checklist)

}
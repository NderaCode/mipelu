package com.cocido.mipelu.data.local.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkRecordDao {
    @Query("SELECT * FROM work_records WHERE ownerUserId = :ownerUserId ORDER BY date DESC")
    fun observeAll(ownerUserId: String): Flow<List<WorkRecordEntity>>

    @Query("SELECT * FROM work_records WHERE clientId = :clientId ORDER BY date DESC")
    fun observeForClient(clientId: String): Flow<List<WorkRecordEntity>>

    @Query("SELECT * FROM work_records WHERE id = :id")
    fun observeById(id: String): Flow<WorkRecordEntity?>

    @Query("SELECT * FROM work_records WHERE id = :id")
    suspend fun getById(id: String): WorkRecordEntity?

    @Query("SELECT COUNT(*) FROM work_records WHERE ownerUserId = :ownerUserId")
    suspend fun count(ownerUserId: String): Int

    @Upsert
    suspend fun upsert(entity: WorkRecordEntity)

    @Upsert
    suspend fun upsertAll(entities: List<WorkRecordEntity>)

    @Query("DELETE FROM work_records WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM work_records WHERE ownerUserId = :ownerUserId AND id NOT IN (:keepIds)")
    suspend fun deleteMissing(ownerUserId: String, keepIds: List<String>)

    @Query("DELETE FROM work_records")
    suspend fun deleteAll()
}

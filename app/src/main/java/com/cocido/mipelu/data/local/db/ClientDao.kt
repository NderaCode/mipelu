package com.cocido.mipelu.data.local.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {
    @Query("SELECT * FROM clients WHERE ownerUserId = :ownerUserId ORDER BY name COLLATE NOCASE ASC")
    fun observeAll(ownerUserId: String): Flow<List<ClientEntity>>

    @Query("SELECT * FROM clients WHERE id = :id")
    fun observeById(id: String): Flow<ClientEntity?>

    @Query("SELECT COUNT(*) FROM clients WHERE ownerUserId = :ownerUserId")
    suspend fun count(ownerUserId: String): Int

    @Upsert
    suspend fun upsert(entity: ClientEntity)

    @Upsert
    suspend fun upsertAll(entities: List<ClientEntity>)

    @Query("DELETE FROM clients WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("DELETE FROM clients WHERE ownerUserId = :ownerUserId AND id NOT IN (:keepIds)")
    suspend fun deleteMissing(ownerUserId: String, keepIds: List<String>)

    @Query("DELETE FROM clients")
    suspend fun deleteAll()
}

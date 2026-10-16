package com.cocido.mipelu.data.local.db

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profiles WHERE id = :id")
    fun observeById(id: String): Flow<UserProfileEntity?>

    @Upsert
    suspend fun upsert(entity: UserProfileEntity)

    @Query("DELETE FROM user_profiles")
    suspend fun deleteAll()
}

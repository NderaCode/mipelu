package com.cocido.mipelu.data.local

import com.cocido.mipelu.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileLocalStore {
    fun observeProfile(userId: String): Flow<UserProfile?>
    suspend fun upsert(profile: UserProfile)
    suspend fun clear()
}

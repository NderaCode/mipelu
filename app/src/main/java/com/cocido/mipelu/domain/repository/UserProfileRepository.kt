package com.cocido.mipelu.domain.repository

import com.cocido.mipelu.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface UserProfileRepository {
    fun observeProfile(userId: String): Flow<UserProfile?>
    suspend fun updateProfile(profile: UserProfile)

    /** Drops any cached data so the next observe() re-fetches. Call on logout. */
    fun clearCache()
}

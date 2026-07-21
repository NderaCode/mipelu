package com.cocido.mipelu.data.local.fake

import com.cocido.mipelu.domain.model.UserProfile
import com.cocido.mipelu.domain.repository.UserProfileRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

@Singleton
class FakeUserProfileRepository @Inject constructor() : UserProfileRepository {

    private val profiles = MutableStateFlow(SeedData.initialProfiles)

    override fun observeProfile(userId: String): Flow<UserProfile?> =
        profiles.map { it[userId] }

    override suspend fun updateProfile(profile: UserProfile) {
        profiles.update { it + (profile.id to profile) }
    }

    fun getProfile(userId: String): UserProfile? = profiles.value[userId]

    override fun clearCache() = Unit
}

package com.cocido.mipelu.data.remote

import com.cocido.mipelu.data.local.UserProfileLocalStore
import com.cocido.mipelu.data.remote.api.MiPeluApi
import com.cocido.mipelu.data.remote.dto.UpdateProfileRequest
import com.cocido.mipelu.data.remote.mapper.toDomain
import com.cocido.mipelu.domain.model.UserProfile
import com.cocido.mipelu.domain.repository.UserProfileRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
class RemoteUserProfileRepository @Inject constructor(
    private val api: MiPeluApi,
    private val local: UserProfileLocalStore,
) : UserProfileRepository {

    private val refreshMutex = Mutex()
    private var hasLoadedOnce = false

    override fun observeProfile(userId: String): Flow<UserProfile?> =
        local.observeProfile(userId).onStart { if (!hasLoadedOnce) refresh() }

    override suspend fun updateProfile(profile: UserProfile) {
        val dto = safeApiCall {
            api.updateProfile(
                UpdateProfileRequest(
                    name = profile.name.ifBlank { null },
                    professionalName = profile.professionalName.ifBlank { null },
                    email = profile.email.ifBlank { null },
                ),
            )
        }
        val storage = runCatching { safeApiCall { api.getStorageUsage() } }.getOrNull()
        local.upsert(dto.toDomain(storage))
        hasLoadedOnce = true
    }

    private suspend fun refresh() = refreshMutex.withLock {
        if (hasLoadedOnce) return@withLock
        val dto = safeApiCall { api.getProfile() }
        val storage = runCatching { safeApiCall { api.getStorageUsage() } }.getOrNull()
        local.upsert(dto.toDomain(storage))
        hasLoadedOnce = true
    }

    override fun clearCache() {
        hasLoadedOnce = false
        runBlocking { local.clear() }
    }
}

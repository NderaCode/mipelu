package com.cocido.mipelu.data.remote

import com.cocido.mipelu.data.di.ApplicationScope
import com.cocido.mipelu.data.remote.api.MiPeluApi
import com.cocido.mipelu.data.remote.auth.TokenStore
import com.cocido.mipelu.data.remote.dto.LoginRequest
import com.cocido.mipelu.data.remote.dto.RefreshTokenRequest
import com.cocido.mipelu.data.remote.dto.SignupRequest
import com.cocido.mipelu.data.remote.dto.StorageUsageDto
import com.cocido.mipelu.data.remote.mapper.toDomain
import com.cocido.mipelu.domain.model.UserProfile
import com.cocido.mipelu.domain.repository.AuthRepository
import com.cocido.mipelu.domain.repository.ClientRepository
import com.cocido.mipelu.domain.repository.UserProfileRepository
import com.cocido.mipelu.domain.repository.WorkRecordRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Singleton
class RemoteAuthRepository @Inject constructor(
    private val api: MiPeluApi,
    private val tokenStore: TokenStore,
    private val clientRepository: ClientRepository,
    private val workRecordRepository: WorkRecordRepository,
    private val userProfileRepository: UserProfileRepository,
    @ApplicationScope private val appScope: CoroutineScope,
) : AuthRepository {

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    override val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    init {
        // Auto-login: if a session was persisted from a previous run, restore it so
        // SplashScreen (which just checks currentUser.value != null) skips the login screen.
        appScope.launch { restoreSession() }
    }

    private suspend fun restoreSession() {
        if (tokenStore.accessToken == null && tokenStore.refreshToken == null) return
        try {
            val profileDto = safeApiCall { api.getProfile() }
            val storage = fetchStorageUsageOrNull()
            _currentUser.value = profileDto.toDomain(storage)
        } catch (e: Exception) {
            // Expired/revoked token that TokenAuthenticator couldn't refresh, or offline at
            // cold start - either way, fall back to the login screen instead of a stuck app.
            tokenStore.clear()
            _currentUser.value = null
        }
    }

    override suspend fun login(email: String, password: String): Result<UserProfile> {
        return try {
            val response = safeApiCall { api.login(LoginRequest(email.trim(), password)) }
            tokenStore.saveTokens(response.accessToken, response.refreshToken)
            val profile = response.user.toDomain(fetchStorageUsageOrNull())
            _currentUser.value = profile
            Result.success(profile)
        } catch (e: MiPeluApiException) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(
        name: String,
        email: String,
        password: String,
        professionalName: String,
    ): Result<UserProfile> {
        return try {
            val response = safeApiCall {
                api.signup(SignupRequest(name, email.trim(), password, professionalName))
            }
            tokenStore.saveTokens(response.accessToken, response.refreshToken)
            val profile = response.user.toDomain(fetchStorageUsageOrNull())
            _currentUser.value = profile
            Result.success(profile)
        } catch (e: MiPeluApiException) {
            Result.failure(e)
        }
    }

    override fun logout() {
        val refreshToken = tokenStore.refreshToken
        _currentUser.value = null
        tokenStore.clear()
        // Otherwise the next login (same or different account, same device) would keep serving
        // this session's cached clients/work records/profile until process death.
        clientRepository.clearCache()
        workRecordRepository.clearCache()
        userProfileRepository.clearCache()
        if (refreshToken != null) {
            // Fire-and-forget: revoke server-side too, but don't make the user wait for it.
            appScope.launch {
                runCatching { safeApiCall { api.logout(RefreshTokenRequest(refreshToken)) } }
            }
        }
    }

    private suspend fun fetchStorageUsageOrNull(): StorageUsageDto? =
        runCatching { safeApiCall { api.getStorageUsage() } }.getOrNull()
}

package com.cocido.mipelu.data.local.fake

import com.cocido.mipelu.domain.model.UserProfile
import com.cocido.mipelu.domain.repository.AuthRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class FakeAuthRepository @Inject constructor(
    private val userProfileRepository: FakeUserProfileRepository,
) : AuthRepository {

    // email normalizado -> (userId, password). Sin backend: solo vive en memoria.
    private val credentials = mutableMapOf(
        SeedData.DEMO_EMAIL to (SeedData.DEMO_USER_ID to SeedData.DEMO_PASSWORD),
    )

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    override val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    override suspend fun login(email: String, password: String): Result<UserProfile> {
        val normalizedEmail = email.trim().lowercase()
        val entry = credentials[normalizedEmail]
        if (entry == null || entry.second != password) {
            return Result.failure(IllegalArgumentException("Email o contraseña incorrectos"))
        }
        val profile = userProfileRepository.getProfile(entry.first)
            ?: return Result.failure(IllegalStateException("No se encontró el perfil"))
        _currentUser.value = profile
        return Result.success(profile)
    }

    override suspend fun signUp(
        name: String,
        email: String,
        password: String,
        professionalName: String,
    ): Result<UserProfile> {
        val normalizedEmail = email.trim().lowercase()
        if (credentials.containsKey(normalizedEmail)) {
            return Result.failure(IllegalStateException("Ya existe una cuenta con ese email"))
        }
        val userId = UUID.randomUUID().toString()
        val profile = UserProfile(
            id = userId,
            name = name,
            email = normalizedEmail,
            professionalName = professionalName.ifBlank { name },
        )
        credentials[normalizedEmail] = userId to password
        userProfileRepository.updateProfile(profile)
        _currentUser.value = profile
        return Result.success(profile)
    }

    override fun logout() {
        _currentUser.value = null
    }
}

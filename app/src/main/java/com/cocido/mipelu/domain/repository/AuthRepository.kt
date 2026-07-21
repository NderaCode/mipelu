package com.cocido.mipelu.domain.repository

import com.cocido.mipelu.domain.model.UserProfile
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
    val currentUser: StateFlow<UserProfile?>

    suspend fun login(email: String, password: String): Result<UserProfile>

    suspend fun signUp(
        name: String,
        email: String,
        password: String,
        professionalName: String,
    ): Result<UserProfile>

    fun logout()
}

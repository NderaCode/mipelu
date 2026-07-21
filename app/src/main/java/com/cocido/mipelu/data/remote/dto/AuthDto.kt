package com.cocido.mipelu.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SignupRequest(
    val name: String,
    val email: String,
    val password: String,
    val professionalName: String,
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)

@Serializable
data class RefreshTokenRequest(
    val refreshToken: String,
)

@Serializable
data class ForgotPasswordRequest(
    val email: String,
)

/** Mirrors src/auth/dto/auth-response.dto.ts: POST /auth/signup, /login and /refresh all return this. */
@Serializable
data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val user: UserProfileDto,
)

package com.cocido.mipelu.data.remote.dto

import kotlinx.serialization.Serializable

/** Mirrors src/users/dto/user-profile.dto.ts on the backend. */
@Serializable
data class UserProfileDto(
    val id: String,
    val name: String,
    val email: String,
    val professionalName: String,
    val plan: String,
    val createdAt: String,
)

/** Mirrors src/users/dto/storage-usage.dto.ts. */
@Serializable
data class StorageUsageDto(
    val usedBytes: Long,
    val limitBytes: Long,
)

/** Body for PATCH /me. All fields optional. */
@Serializable
data class UpdateProfileRequest(
    val name: String? = null,
    val professionalName: String? = null,
    val email: String? = null,
)

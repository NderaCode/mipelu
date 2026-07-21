package com.cocido.mipelu.data.remote.mapper

import com.cocido.mipelu.data.remote.dto.StorageUsageDto
import com.cocido.mipelu.data.remote.dto.UserProfileDto
import com.cocido.mipelu.domain.model.UserProfile

private const val DEFAULT_STORAGE_LIMIT_BYTES = 1024L * 1024 * 1024

/** photoUrl stays null: the backend has no profile-photo concept yet. */
fun UserProfileDto.toDomain(storage: StorageUsageDto?): UserProfile = UserProfile(
    id = id,
    name = name,
    email = email,
    professionalName = professionalName,
    photoUrl = null,
    plan = plan,
    storageUsedBytes = storage?.usedBytes ?: 0L,
    storageLimitBytes = storage?.limitBytes ?: DEFAULT_STORAGE_LIMIT_BYTES,
    createdAt = createdAt.isoDateTimeToMillis(),
)

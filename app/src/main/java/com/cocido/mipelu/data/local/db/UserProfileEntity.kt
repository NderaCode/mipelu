package com.cocido.mipelu.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profiles")
data class UserProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String,
    val professionalName: String,
    val photoUrl: String?,
    val plan: String,
    val emailVerified: Boolean,
    val storageUsedBytes: Long,
    val storageLimitBytes: Long,
    val createdAt: Long,
)

package com.cocido.mipelu.domain.model

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val professionalName: String = "",
    val photoUrl: String? = null,
    val plan: String = "free",
    val storageUsedBytes: Long = 0L,
    val storageLimitBytes: Long = 2L * 1024 * 1024 * 1024,
    val createdAt: Long = System.currentTimeMillis(),
) {
    val initials: String
        get() = name.trim().firstOrNull()?.uppercase() ?: "?"
}

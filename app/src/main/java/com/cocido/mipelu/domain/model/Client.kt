package com.cocido.mipelu.domain.model

data class Client(
    val id: String,
    val ownerUserId: String,
    val name: String,
    val phone: String = "",
    val importantNotes: String = "",
    val hairType: String = "",
    val baseColor: String = "",
    val sensitivity: String = "",
    val preferences: String = "",
    val allergies: String = "",
    val porosity: String = "",
    val productsThatWorked: String = "",
    val productsToAvoid: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
) {
    val initials: String
        get() = name.trim()
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
            .ifBlank { "?" }
}

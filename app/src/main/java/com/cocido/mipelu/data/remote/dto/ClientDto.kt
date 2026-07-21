package com.cocido.mipelu.data.remote.dto

import kotlinx.serialization.Serializable

/** Mirrors src/clients/dto/client-list-item.dto.ts. */
@Serializable
data class ClientListItemDto(
    val id: String,
    val name: String,
    val phone: String? = null,
    val initials: String,
    val lastWorkType: String? = null,
    val lastWorkDate: String? = null,
    val tags: List<String> = emptyList(),
)

@Serializable
data class ClientListResponse(
    val items: List<ClientListItemDto>,
    val total: Int,
)

@Serializable
data class ClientHistorySummaryDto(
    val workRecordsCount: Int,
    val lastWorkDate: String? = null,
    val tags: List<String> = emptyList(),
)

/** Mirrors src/clients/dto/client-detail.dto.ts (GET/POST/PATCH /clients responses). */
@Serializable
data class ClientDetailDto(
    val id: String,
    val name: String,
    val phone: String? = null,
    val importantNotes: String? = null,
    val hairType: String? = null,
    val baseColor: String? = null,
    val porosity: String? = null,
    val sensitivity: String? = null,
    val allergies: String? = null,
    val preferences: String? = null,
    val productsThatWorked: String? = null,
    val productsToAvoid: String? = null,
    val observations: String? = null,
    val createdAt: String,
    val updatedAt: String,
    val historySummary: ClientHistorySummaryDto? = null,
)

/** Body for POST /clients and PATCH /clients/:id (all fields optional on update). */
@Serializable
data class ClientUpsertRequest(
    val name: String,
    val phone: String? = null,
    val importantNotes: String? = null,
    val hairType: String? = null,
    val baseColor: String? = null,
    val porosity: String? = null,
    val sensitivity: String? = null,
    val allergies: String? = null,
    val preferences: String? = null,
    val productsThatWorked: String? = null,
    val productsToAvoid: String? = null,
    val observations: String? = null,
)

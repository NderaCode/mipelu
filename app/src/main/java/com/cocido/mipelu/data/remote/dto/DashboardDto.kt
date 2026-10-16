package com.cocido.mipelu.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Compact home-screen card. Distinct from [WorkRecordListItemDto], which now carries the full
 * work so the list can populate Room without N+1 GETs.
 */
@Serializable
data class DashboardWorkRecordDto(
    val id: String,
    val clientId: String,
    val clientName: String,
    val serviceTypes: List<String>,
    val date: String,
    val formulaSummary: String? = null,
    val beforeThumbnailUrl: String? = null,
    val afterThumbnailUrl: String? = null,
    val isDraft: Boolean,
)

/** Mirrors src/dashboard/dto/dashboard-response.dto.ts. Not consumed yet (HomeViewModel still
 * derives its counts from the cached clients/work-records lists), but kept ready so a future
 * pass can call GET /dashboard directly instead.
 */
@Serializable
data class DashboardResponse(
    val clientsCount: Int,
    val workRecordsCount: Int,
    val photosCount: Int,
    val latestWorkRecords: List<DashboardWorkRecordDto>,
)

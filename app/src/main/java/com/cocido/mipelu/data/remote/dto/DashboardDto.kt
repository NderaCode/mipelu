package com.cocido.mipelu.data.remote.dto

import kotlinx.serialization.Serializable

/**
 * Mirrors src/dashboard/dto/dashboard-response.dto.ts. Not consumed yet (HomeViewModel still
 * derives its counts from the cached clients/work-records lists), but kept ready so a future
 * pass can call GET /dashboard directly instead.
 */
@Serializable
data class DashboardResponse(
    val clientsCount: Int,
    val workRecordsCount: Int,
    val photosCount: Int,
    val latestWorkRecords: List<WorkRecordListItemDto>,
)

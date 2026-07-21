package com.cocido.mipelu.data.remote.dto

import kotlinx.serialization.Serializable

/** Mirrors src/photos/dto/photo-response.dto.ts. type is "before" | "after". */
@Serializable
data class PhotoDto(
    val id: String,
    val type: String,
    val url: String,
    val thumbnailUrl: String? = null,
    val sizeBytes: Long,
    val createdAt: String,
)

/** Mirrors src/work-records/dto/work-record-list-item.dto.ts. */
@Serializable
data class WorkRecordListItemDto(
    val id: String,
    val clientId: String,
    val clientName: String,
    val serviceType: String,
    val date: String,
    val formulaSummary: String? = null,
    val beforeThumbnailUrl: String? = null,
    val afterThumbnailUrl: String? = null,
    val isDraft: Boolean,
)

@Serializable
data class WorkRecordListResponse(
    val items: List<WorkRecordListItemDto>,
    val total: Int,
)

/** Mirrors src/work-records/dto/work-record-detail.dto.ts (GET/POST/PATCH /work-records responses). */
@Serializable
data class WorkRecordDetailDto(
    val id: String,
    val clientId: String,
    val clientName: String,
    val serviceType: String,
    val date: String,
    val hairCondition: String? = null,
    val baseColor: String? = null,
    val objective: String? = null,
    val diagnosisNotes: String? = null,
    val formula: String? = null,
    val products: String? = null,
    val oxidantVolume: String? = null,
    val exposureTime: String? = null,
    val technique: String? = null,
    val result: String? = null,
    val price: Double? = null,
    val recommendations: String? = null,
    val nextFollowUpNote: String? = null,
    val isDraft: Boolean,
    val createdAt: String,
    val updatedAt: String,
    val photos: List<PhotoDto> = emptyList(),
)

/** Body for POST /work-records. */
@Serializable
data class CreateWorkRecordRequest(
    val clientId: String,
    val serviceType: String,
    val date: String,
    val hairCondition: String? = null,
    val baseColor: String? = null,
    val objective: String? = null,
    val diagnosisNotes: String? = null,
    val formula: String? = null,
    val products: String? = null,
    val oxidantVolume: String? = null,
    val exposureTime: String? = null,
    val technique: String? = null,
    val result: String? = null,
    val price: Double? = null,
    val recommendations: String? = null,
    val nextFollowUpNote: String? = null,
    val isDraft: Boolean = false,
)

/** Body for PATCH /work-records/:id - same shape minus clientId, all optional. */
@Serializable
data class UpdateWorkRecordRequest(
    val serviceType: String? = null,
    val date: String? = null,
    val hairCondition: String? = null,
    val baseColor: String? = null,
    val objective: String? = null,
    val diagnosisNotes: String? = null,
    val formula: String? = null,
    val products: String? = null,
    val oxidantVolume: String? = null,
    val exposureTime: String? = null,
    val technique: String? = null,
    val result: String? = null,
    val price: Double? = null,
    val recommendations: String? = null,
    val nextFollowUpNote: String? = null,
    val isDraft: Boolean? = null,
)

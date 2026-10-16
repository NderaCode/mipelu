package com.cocido.mipelu.data.local.db

import kotlinx.serialization.Serializable

/** Photo row as stored next to a [WorkRecordEntity]. Mirrors the backend PhotoDto without
 * dragging Retrofit types into the local layer. */
@Serializable
data class CachedPhoto(
    val id: String,
    val type: String,
    val url: String,
    val thumbnailUrl: String? = null,
)

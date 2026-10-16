package com.cocido.mipelu.data.local.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "work_records",
    indices = [
        Index("ownerUserId"),
        Index("clientId"),
    ],
)
data class WorkRecordEntity(
    @PrimaryKey val id: String,
    val ownerUserId: String,
    val clientId: String,
    val clientName: String,
    val serviceTypesJson: String,
    val date: Long,
    val hairCondition: String,
    val baseColor: String,
    val objective: String,
    val formula: String,
    val productsUsed: String,
    val oxidantVolume: String,
    val exposureTime: String,
    val technique: String,
    val finalResult: String,
    val price: String,
    val recommendations: String,
    val nextFollowUpNote: String,
    val notes: String,
    val isDraft: Boolean,
    val createdAt: Long,
    val updatedAt: Long,
    /** JSON array of CachedPhoto - kept next to the work so uploadPhoto can still find photo ids
     * offline without a round trip, and so before/after URLs survive a restart. */
    val photosJson: String,
)

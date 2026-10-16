package com.cocido.mipelu.data.local.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "clients",
    indices = [Index("ownerUserId")],
)
data class ClientEntity(
    @PrimaryKey val id: String,
    val ownerUserId: String,
    val name: String,
    val phone: String,
    val importantNotes: String,
    val hairType: String,
    val baseColor: String,
    val sensitivity: String,
    val preferences: String,
    val allergies: String,
    val porosity: String,
    val productsThatWorked: String,
    val productsToAvoid: String,
    val createdAt: Long,
    val updatedAt: Long,
)

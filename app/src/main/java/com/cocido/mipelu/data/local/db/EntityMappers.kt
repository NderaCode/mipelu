package com.cocido.mipelu.data.local.db

import com.cocido.mipelu.domain.model.Client
import com.cocido.mipelu.domain.model.ServiceType
import com.cocido.mipelu.domain.model.UserProfile
import com.cocido.mipelu.domain.model.WorkRecord
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

fun ClientEntity.toDomain(): Client = Client(
    id = id,
    ownerUserId = ownerUserId,
    name = name,
    phone = phone,
    importantNotes = importantNotes,
    hairType = hairType,
    baseColor = baseColor,
    sensitivity = sensitivity,
    preferences = preferences,
    allergies = allergies,
    porosity = porosity,
    productsThatWorked = productsThatWorked,
    productsToAvoid = productsToAvoid,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun Client.toEntity(): ClientEntity = ClientEntity(
    id = id,
    ownerUserId = ownerUserId,
    name = name,
    phone = phone,
    importantNotes = importantNotes,
    hairType = hairType,
    baseColor = baseColor,
    sensitivity = sensitivity,
    preferences = preferences,
    allergies = allergies,
    porosity = porosity,
    productsThatWorked = productsThatWorked,
    productsToAvoid = productsToAvoid,
    createdAt = createdAt,
    updatedAt = updatedAt,
)

fun WorkRecordEntity.toDomain(): WorkRecord {
    val photos = decodePhotos()
    return WorkRecord(
        id = id,
        ownerUserId = ownerUserId,
        clientId = clientId,
        clientName = clientName,
        serviceTypes = json.decodeFromString<List<String>>(serviceTypesJson).map { stored ->
            runCatching { ServiceType.valueOf(stored) }.getOrDefault(ServiceType.COLOR)
        },
        date = date,
        hairCondition = hairCondition,
        baseColor = baseColor,
        objective = objective,
        formula = formula,
        productsUsed = productsUsed,
        oxidantVolume = oxidantVolume,
        exposureTime = exposureTime,
        technique = technique,
        finalResult = finalResult,
        price = price,
        recommendations = recommendations,
        nextFollowUpNote = nextFollowUpNote,
        beforePhotoUrls = photos.filter { it.type == "before" }.map { it.thumbnailUrl ?: it.url },
        afterPhotoUrls = photos.filter { it.type == "after" }.map { it.thumbnailUrl ?: it.url },
        notes = notes,
        isDraft = isDraft,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
}

fun WorkRecordEntity.decodePhotos(): List<CachedPhoto> =
    runCatching { json.decodeFromString<List<CachedPhoto>>(photosJson) }.getOrDefault(emptyList())

fun WorkRecord.toEntity(photos: List<CachedPhoto>): WorkRecordEntity = WorkRecordEntity(
    id = id,
    ownerUserId = ownerUserId,
    clientId = clientId,
    clientName = clientName,
    serviceTypesJson = json.encodeToString(serviceTypes.map { it.name }),
    date = date,
    hairCondition = hairCondition,
    baseColor = baseColor,
    objective = objective,
    formula = formula,
    productsUsed = productsUsed,
    oxidantVolume = oxidantVolume,
    exposureTime = exposureTime,
    technique = technique,
    finalResult = finalResult,
    price = price,
    recommendations = recommendations,
    nextFollowUpNote = nextFollowUpNote,
    notes = notes,
    isDraft = isDraft,
    createdAt = createdAt,
    updatedAt = updatedAt,
    photosJson = json.encodeToString(photos),
)

fun UserProfileEntity.toDomain(): UserProfile = UserProfile(
    id = id,
    name = name,
    email = email,
    professionalName = professionalName,
    photoUrl = photoUrl,
    plan = plan,
    emailVerified = emailVerified,
    storageUsedBytes = storageUsedBytes,
    storageLimitBytes = storageLimitBytes,
    createdAt = createdAt,
)

fun UserProfile.toEntity(): UserProfileEntity = UserProfileEntity(
    id = id,
    name = name,
    email = email,
    professionalName = professionalName,
    photoUrl = photoUrl,
    plan = plan,
    emailVerified = emailVerified,
    storageUsedBytes = storageUsedBytes,
    storageLimitBytes = storageLimitBytes,
    createdAt = createdAt,
)

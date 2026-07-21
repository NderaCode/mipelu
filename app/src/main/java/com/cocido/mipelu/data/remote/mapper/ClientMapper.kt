package com.cocido.mipelu.data.remote.mapper

import com.cocido.mipelu.data.remote.dto.ClientDetailDto
import com.cocido.mipelu.data.remote.dto.ClientUpsertRequest
import com.cocido.mipelu.domain.model.Client

/**
 * The backend also has an `observations` field with no Android UI counterpart yet - it's
 * dropped on read and never sent on write (always null). Everything else maps 1:1.
 */
fun ClientDetailDto.toDomain(ownerUserId: String): Client = Client(
    id = id,
    ownerUserId = ownerUserId,
    name = name,
    phone = phone.orEmpty(),
    importantNotes = importantNotes.orEmpty(),
    hairType = hairType.orEmpty(),
    baseColor = baseColor.orEmpty(),
    sensitivity = sensitivity.orEmpty(),
    preferences = preferences.orEmpty(),
    allergies = allergies.orEmpty(),
    porosity = porosity.orEmpty(),
    productsThatWorked = productsThatWorked.orEmpty(),
    productsToAvoid = productsToAvoid.orEmpty(),
    createdAt = createdAt.isoDateTimeToMillis(),
    updatedAt = updatedAt.isoDateTimeToMillis(),
)

fun Client.toUpsertRequest(): ClientUpsertRequest = ClientUpsertRequest(
    name = name,
    phone = phone.ifBlank { null },
    importantNotes = importantNotes.ifBlank { null },
    hairType = hairType.ifBlank { null },
    baseColor = baseColor.ifBlank { null },
    porosity = porosity.ifBlank { null },
    sensitivity = sensitivity.ifBlank { null },
    allergies = allergies.ifBlank { null },
    preferences = preferences.ifBlank { null },
    productsThatWorked = productsThatWorked.ifBlank { null },
    productsToAvoid = productsToAvoid.ifBlank { null },
)

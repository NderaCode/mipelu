package com.cocido.mipelu.data.remote.mapper

import com.cocido.mipelu.data.remote.dto.CreateWorkRecordRequest
import com.cocido.mipelu.data.remote.dto.UpdateWorkRecordRequest
import com.cocido.mipelu.data.remote.dto.WorkRecordDetailDto
import com.cocido.mipelu.domain.model.WorkRecord
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun WorkRecordDetailDto.toDomain(ownerUserId: String): WorkRecord {
    val beforeUrls = photos.filter { it.type == "before" }.map { it.url }
    val afterUrls = photos.filter { it.type == "after" }.map { it.url }
    return WorkRecord(
        id = id,
        ownerUserId = ownerUserId,
        clientId = clientId,
        clientName = clientName,
        serviceTypes = serviceTypes.map { it.toServiceType() },
        date = date.isoDateToMillis(),
        hairCondition = hairCondition.orEmpty(),
        baseColor = baseColor.orEmpty(),
        objective = objective.orEmpty(),
        formula = formula.orEmpty(),
        productsUsed = products.orEmpty(),
        oxidantVolume = oxidantVolume.orEmpty(),
        exposureTime = exposureTime.orEmpty(),
        technique = technique.orEmpty(),
        finalResult = result.orEmpty(),
        price = price?.let(::formatPrice).orEmpty(),
        recommendations = recommendations.orEmpty(),
        nextFollowUpNote = nextFollowUpNote.orEmpty(),
        beforePhotoUrls = beforeUrls,
        afterPhotoUrls = afterUrls,
        // "notes" in the Android domain model IS diagnosisNotes (see WorkFieldSpecs.kt:
        // label "Notas del diagnóstico" bound to WorkRecord.notes) - same field, different name.
        notes = diagnosisNotes.orEmpty(),
        isDraft = isDraft,
        createdAt = createdAt.isoDateTimeToMillis(),
        updatedAt = updatedAt.isoDateTimeToMillis(),
    )
}

fun WorkRecord.toCreateRequest(): CreateWorkRecordRequest = CreateWorkRecordRequest(
    clientId = clientId,
    serviceTypes = serviceTypes.map { it.toWireValue() },
    date = date.toIsoDate(),
    hairCondition = hairCondition.ifBlank { null },
    baseColor = baseColor.ifBlank { null },
    objective = objective.ifBlank { null },
    diagnosisNotes = notes.ifBlank { null },
    formula = formula.ifBlank { null },
    products = productsUsed.ifBlank { null },
    oxidantVolume = oxidantVolume.ifBlank { null },
    exposureTime = exposureTime.ifBlank { null },
    technique = technique.ifBlank { null },
    result = finalResult.ifBlank { null },
    price = price.toBackendPrice(),
    recommendations = recommendations.ifBlank { null },
    nextFollowUpNote = nextFollowUpNote.ifBlank { null },
    isDraft = isDraft,
)

fun WorkRecord.toUpdateRequest(): UpdateWorkRecordRequest = UpdateWorkRecordRequest(
    serviceTypes = serviceTypes.map { it.toWireValue() },
    date = date.toIsoDate(),
    hairCondition = hairCondition.ifBlank { null },
    baseColor = baseColor.ifBlank { null },
    objective = objective.ifBlank { null },
    diagnosisNotes = notes.ifBlank { null },
    formula = formula.ifBlank { null },
    products = productsUsed.ifBlank { null },
    oxidantVolume = oxidantVolume.ifBlank { null },
    exposureTime = exposureTime.ifBlank { null },
    technique = technique.ifBlank { null },
    result = finalResult.ifBlank { null },
    price = price.toBackendPrice(),
    recommendations = recommendations.ifBlank { null },
    nextFollowUpNote = nextFollowUpNote.ifBlank { null },
    isDraft = isDraft,
)

/**
 * The Android UI keeps price as free text (placeholder "$ 20.000"), the backend wants a plain
 * number. Heuristic: "," always means decimal point (es-AR never uses it as a thousands
 * separator in this field). Without a comma, a "." is only treated as a thousands separator when
 * it's followed by exactly 3 digits (real es-AR grouping, e.g. "20.000") or there's more than one
 * "." (e.g. "1.234.567") - a single "." followed by 1-2 digits (e.g. "20.5") reads as a decimal
 * point to virtually every user, so it's left alone instead of being silently turned into "205".
 * Good enough for the MVP text field, not a full currency parser.
 */
fun String.toBackendPrice(): Double? {
    val trimmed = trim().replace("$", "").trim()
    if (trimmed.isBlank()) return null

    val dotCount = trimmed.count { it == '.' }
    val normalized = when {
        trimmed.contains(',') -> trimmed.replace(".", "").replace(",", ".")
        dotCount == 1 && trimmed.substringAfterLast('.').length != 3 -> trimmed
        dotCount >= 1 -> trimmed.replace(".", "")
        else -> trimmed
    }
    return normalized.filter { it.isDigit() || it == '.' }.toDoubleOrNull()
}

// es-AR formatting: "." as thousands separator, "," as decimal - mirrors what the user typed
// on the way in (toBackendPrice above), so round-tripping through the backend doesn't turn
// "$ 19.500" into a bare "19500".
private val priceSymbols = DecimalFormatSymbols(Locale.Builder().setLanguage("es").setRegion("AR").build()).apply {
    groupingSeparator = '.'
    decimalSeparator = ','
}
private val wholePriceFormat = DecimalFormat("#,##0", priceSymbols)
private val decimalPriceFormat = DecimalFormat("#,##0.00", priceSymbols)

private fun formatPrice(value: Double): String {
    val formatted = if (value == value.toLong().toDouble()) {
        wholePriceFormat.format(value)
    } else {
        decimalPriceFormat.format(value)
    }
    return "$ $formatted"
}

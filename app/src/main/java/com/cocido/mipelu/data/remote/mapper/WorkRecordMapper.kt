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
        serviceType = serviceType.toServiceType(),
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
    serviceType = serviceType.toWireValue(),
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
    serviceType = serviceType.toWireValue(),
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
 * number. Heuristic: "." is treated as an es-AR thousands separator and stripped, "," becomes
 * the decimal point. Good enough for the MVP text field, not a full currency parser.
 */
fun String.toBackendPrice(): Double? {
    val cleaned = trim()
        .replace("$", "")
        .replace(".", "")
        .replace(",", ".")
        .filter { it.isDigit() || it == '.' }
    return cleaned.toDoubleOrNull()
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

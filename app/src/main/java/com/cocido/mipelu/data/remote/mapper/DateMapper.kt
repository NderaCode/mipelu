package com.cocido.mipelu.data.remote.mapper

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/** Backend "date" fields (WorkRecord.date) are date-only ISO strings, e.g. "2026-07-20". */
fun Long.toIsoDate(): String =
    Instant.ofEpochMilli(this).atZone(ZoneOffset.UTC).toLocalDate().toString()

fun String.isoDateToMillis(): Long =
    LocalDate.parse(this).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

/** createdAt/updatedAt are full ISO-8601 datetime strings, e.g. "2026-07-20T15:04:33.123Z". */
fun String.isoDateTimeToMillis(): Long = Instant.parse(this).toEpochMilli()

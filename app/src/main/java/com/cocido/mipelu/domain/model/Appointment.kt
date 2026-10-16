package com.cocido.mipelu.domain.model

data class WeeklyWindow(
    val weekday: Int,
    val startTime: String,
    val endTime: String,
)

data class BookingSettings(
    val slug: String,
    val publicUrl: String,
    val timezone: String,
    val slotMinutes: Int,
    val bufferMinutes: Int,
    val minNoticeMinutes: Int,
    val horizonDays: Int,
    val weekly: List<WeeklyWindow>,
)

data class Appointment(
    val id: String,
    val guestName: String,
    val guestEmail: String,
    val guestPhone: String? = null,
    val clientId: String? = null,
    val startsAt: String,
    val endsAt: String,
    val status: String,
    val notes: String? = null,
)

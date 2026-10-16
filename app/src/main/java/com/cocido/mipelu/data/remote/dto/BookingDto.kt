package com.cocido.mipelu.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class WeeklyWindowDto(
    val weekday: Int,
    val startTime: String,
    val endTime: String,
)

@Serializable
data class BookingSettingsDto(
    val slug: String,
    val publicUrl: String,
    val timezone: String,
    val slotMinutes: Int = 60,
    val bufferMinutes: Int,
    val minNoticeMinutes: Int,
    val horizonDays: Int,
    val weekly: List<WeeklyWindowDto> = emptyList(),
    val eventTypes: List<LegacyEventTypeDto> = emptyList(),
)

@Serializable
data class LegacyEventTypeDto(
    val id: String,
    val name: String,
    val slug: String,
    val durationMinutes: Int,
    val description: String? = null,
    val isActive: Boolean = true,
    val sortOrder: Int = 0,
)

@Serializable
data class UpdateBookingSettingsRequest(
    val weekly: List<WeeklyWindowDto>? = null,
    val slotMinutes: Int? = null,
)

@Serializable
data class AppointmentDto(
    val id: String,
    val guestName: String,
    val guestEmail: String,
    val guestPhone: String? = null,
    val clientId: String? = null,
    val startsAt: String,
    val endsAt: String,
    val status: String,
    val notes: String? = null,
    val eventTypeName: String = "",
    val durationMinutes: Int = 0,
)

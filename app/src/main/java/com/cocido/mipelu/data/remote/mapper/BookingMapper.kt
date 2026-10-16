package com.cocido.mipelu.data.remote.mapper

import com.cocido.mipelu.data.remote.dto.AppointmentDto
import com.cocido.mipelu.data.remote.dto.BookingSettingsDto
import com.cocido.mipelu.data.remote.dto.WeeklyWindowDto
import com.cocido.mipelu.domain.model.Appointment
import com.cocido.mipelu.domain.model.BookingSettings
import com.cocido.mipelu.domain.model.WeeklyWindow

fun WeeklyWindowDto.toDomain() = WeeklyWindow(weekday = weekday, startTime = startTime, endTime = endTime)

fun BookingSettingsDto.toDomain() = BookingSettings(
    slug = slug,
    publicUrl = publicUrl,
    timezone = timezone,
    slotMinutes = slotMinutes,
    bufferMinutes = bufferMinutes,
    minNoticeMinutes = minNoticeMinutes,
    horizonDays = horizonDays,
    weekly = weekly.map { it.toDomain() },
)

fun AppointmentDto.toDomain() = Appointment(
    id = id,
    guestName = guestName,
    guestEmail = guestEmail,
    guestPhone = guestPhone,
    clientId = clientId,
    startsAt = startsAt,
    endsAt = endsAt,
    status = status,
    notes = notes,
)

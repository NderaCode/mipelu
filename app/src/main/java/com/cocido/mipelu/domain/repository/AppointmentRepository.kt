package com.cocido.mipelu.domain.repository

import com.cocido.mipelu.domain.model.Appointment
import com.cocido.mipelu.domain.model.BookingSettings
import com.cocido.mipelu.domain.model.WeeklyWindow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AppointmentRepository {
    val isLoading: StateFlow<Boolean>
    val error: StateFlow<String?>
    fun observeUpcoming(): Flow<List<Appointment>>
    fun observeSettings(): Flow<BookingSettings?>
    suspend fun refresh()
    suspend fun updateWeekly(weekly: List<WeeklyWindow>)
    suspend fun updateSlotMinutes(minutes: Int)
    suspend fun cancel(appointmentId: String)
    fun clearCache()
}

package com.cocido.mipelu.data.remote

import com.cocido.mipelu.data.remote.api.MiPeluApi
import com.cocido.mipelu.data.remote.dto.UpdateBookingSettingsRequest
import com.cocido.mipelu.data.remote.dto.WeeklyWindowDto
import com.cocido.mipelu.data.remote.mapper.toDomain
import com.cocido.mipelu.domain.model.Appointment
import com.cocido.mipelu.domain.model.BookingSettings
import com.cocido.mipelu.domain.model.WeeklyWindow
import com.cocido.mipelu.domain.repository.AppointmentRepository
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
class RemoteAppointmentRepository @Inject constructor(
    private val api: MiPeluApi,
) : AppointmentRepository {

    private val refreshMutex = Mutex()
    private val _upcoming = MutableStateFlow<List<Appointment>>(emptyList())
    private val _settings = MutableStateFlow<BookingSettings?>(null)
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    override val error: StateFlow<String?> = _error.asStateFlow()
    override fun observeUpcoming() = _upcoming.asStateFlow()
    override fun observeSettings() = _settings.asStateFlow()

    override suspend fun refresh() {
        refreshMutex.withLock {
            _isLoading.value = true
            _error.value = null
            try {
                val from = Instant.now().minus(1, ChronoUnit.DAYS).toString()
                val to = Instant.now().plus(60, ChronoUnit.DAYS).toString()
                _settings.value = safeApiCall { api.getBookingSettings() }.toDomain()
                _upcoming.value = safeApiCall { api.listAppointments(from, to) }
                    .map { it.toDomain() }
                    .filter { it.status != "cancelled" }
            } catch (error: MiPeluApiException) {
                _error.value = error.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    override suspend fun updateWeekly(weekly: List<WeeklyWindow>) {
        _settings.value = safeApiCall {
            api.updateBookingSettings(
                UpdateBookingSettingsRequest(
                    weekly = weekly.map { WeeklyWindowDto(it.weekday, it.startTime, it.endTime) },
                ),
            )
        }.toDomain()
    }

    override suspend fun updateSlotMinutes(minutes: Int) {
        _settings.value = safeApiCall {
            api.updateBookingSettings(UpdateBookingSettingsRequest(slotMinutes = minutes))
        }.toDomain()
    }

    override suspend fun cancel(appointmentId: String) {
        safeApiCall { api.cancelAppointment(appointmentId) }
        _upcoming.value = _upcoming.value.filterNot { it.id == appointmentId }
    }

    override fun clearCache() {
        _upcoming.value = emptyList()
        _settings.value = null
        _error.value = null
    }
}

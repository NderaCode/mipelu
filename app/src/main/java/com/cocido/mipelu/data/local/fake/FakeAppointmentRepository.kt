package com.cocido.mipelu.data.local.fake

import com.cocido.mipelu.domain.model.Appointment
import com.cocido.mipelu.domain.model.BookingSettings
import com.cocido.mipelu.domain.model.WeeklyWindow
import com.cocido.mipelu.domain.repository.AppointmentRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class FakeAppointmentRepository @Inject constructor() : AppointmentRepository {
    private val settings = BookingSettings(
        slug = "romi-colorista",
        publicUrl = "https://api.shacode.com.ar/b/romi-colorista",
        timezone = "America/Argentina/Buenos_Aires",
        slotMinutes = 60,
        bufferMinutes = 0,
        minNoticeMinutes = 120,
        horizonDays = 14,
        weekly = (1..6).map { WeeklyWindow(it, "09:00", "18:00") },
    )
    private val _settings = MutableStateFlow<BookingSettings?>(settings)
    private val _upcoming = MutableStateFlow<List<Appointment>>(emptyList())
    private val _loading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)

    override val isLoading: StateFlow<Boolean> = _loading.asStateFlow()
    override val error: StateFlow<String?> = _error.asStateFlow()
    override fun observeUpcoming() = _upcoming.asStateFlow()
    override fun observeSettings() = _settings.asStateFlow()
    override suspend fun refresh() = Unit
    override suspend fun updateWeekly(weekly: List<WeeklyWindow>) {
        _settings.value = _settings.value?.copy(weekly = weekly)
    }
    override suspend fun updateSlotMinutes(minutes: Int) {
        _settings.value = _settings.value?.copy(slotMinutes = minutes)
    }
    override suspend fun cancel(appointmentId: String) {
        _upcoming.value = _upcoming.value.filterNot { it.id == appointmentId }
    }
    override fun clearCache() = Unit
}

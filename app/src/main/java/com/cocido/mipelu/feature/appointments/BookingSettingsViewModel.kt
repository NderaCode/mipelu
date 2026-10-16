package com.cocido.mipelu.feature.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocido.mipelu.domain.model.BookingSettings
import com.cocido.mipelu.domain.model.WeeklyWindow
import com.cocido.mipelu.domain.repository.AppointmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class BookingSettingsViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
) : ViewModel() {

    val settings: StateFlow<BookingSettings?> = appointmentRepository.observeSettings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _saving = MutableStateFlow(false)
    val saving: StateFlow<Boolean> = _saving

    init {
        viewModelScope.launch { appointmentRepository.refresh() }
    }

    fun toggleWeekday(weekday: Int, enabled: Boolean) {
        val current = settings.value ?: return
        val next = if (enabled) {
            current.weekly.filterNot { it.weekday == weekday } + WeeklyWindow(weekday, "09:00", "18:00")
        } else {
            current.weekly.filterNot { it.weekday == weekday }
        }
        viewModelScope.launch {
            _saving.value = true
            runCatching { appointmentRepository.updateWeekly(next.sortedBy { it.weekday }) }
            _saving.value = false
        }
    }

    fun setSlotMinutes(minutes: Int) {
        viewModelScope.launch {
            _saving.value = true
            runCatching { appointmentRepository.updateSlotMinutes(minutes) }
            _saving.value = false
        }
    }
}

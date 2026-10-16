package com.cocido.mipelu.feature.appointments

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocido.mipelu.domain.model.Appointment
import com.cocido.mipelu.domain.model.BookingSettings
import com.cocido.mipelu.domain.repository.AppointmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AgendaUiState(
    val appointments: List<Appointment> = emptyList(),
    val settings: BookingSettings? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
) {
    val isEmpty: Boolean get() = appointments.isEmpty() && !isLoading && error == null
}

@HiltViewModel
class AgendaViewModel @Inject constructor(
    private val appointmentRepository: AppointmentRepository,
) : ViewModel() {

    val uiState: StateFlow<AgendaUiState> = combine(
        appointmentRepository.observeUpcoming(),
        appointmentRepository.observeSettings(),
        appointmentRepository.isLoading,
        appointmentRepository.error,
    ) { appointments, settings, loading, error ->
        AgendaUiState(appointments, settings, loading, error)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AgendaUiState(isLoading = true))

    init {
        viewModelScope.launch { appointmentRepository.refresh() }
    }

    fun refresh() {
        viewModelScope.launch { appointmentRepository.refresh() }
    }

    fun cancel(id: String) {
        viewModelScope.launch { runCatching { appointmentRepository.cancel(id) } }
    }
}

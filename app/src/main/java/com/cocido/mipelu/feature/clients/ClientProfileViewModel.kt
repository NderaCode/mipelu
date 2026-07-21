package com.cocido.mipelu.feature.clients

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocido.mipelu.domain.model.Client
import com.cocido.mipelu.domain.model.WorkRecord
import com.cocido.mipelu.domain.repository.ClientRepository
import com.cocido.mipelu.domain.repository.WorkRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

data class ClientProfileUiState(
    val client: Client? = null,
    val works: List<WorkRecord> = emptyList(),
) {
    val lastHairCondition: String
        get() = works.firstOrNull()?.hairCondition?.ifBlank { null } ?: "Sin registros todavía"
}

@HiltViewModel
class ClientProfileViewModel @Inject constructor(
    clientRepository: ClientRepository,
    workRecordRepository: WorkRecordRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val clientId: String = savedStateHandle.get<String>("clientId").orEmpty()

    val uiState: StateFlow<ClientProfileUiState> = combine(
        clientRepository.observeClient(clientId),
        workRecordRepository.observeWorksForClient(clientId),
    ) { client, works -> ClientProfileUiState(client, works) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ClientProfileUiState())
}

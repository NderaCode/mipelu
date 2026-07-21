@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.cocido.mipelu.feature.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocido.mipelu.domain.model.Client
import com.cocido.mipelu.domain.repository.AuthRepository
import com.cocido.mipelu.domain.repository.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

enum class ClientFilter(val label: String) {
    TODAS("Todas"),
    ALERGIAS("Con alergias"),
    SENSIBLES("Piel sensible"),
}

// "Sin alergias/sensibilidad" se escribe de mil formas distintas en una ficha real
// ("ninguna", "no presenta", "baja", ...) - tratamos cualquier texto que no sea
// claramente una negación/nivel bajo como una señal a mostrar, en vez de buscar
// literalmente la palabra "alta" (que se perdía casos como "cuero cabelludo sensible").
private val NO_ALLERGY_PHRASES = setOf("ninguna", "ninguna conocida", "no", "no tiene", "no presenta", "sin alergias")
private val LOW_SENSITIVITY_PHRASES = setOf("ninguna", "no", "normal", "no presenta", "sin sensibilidad")

private fun Client.hasAllergy(): Boolean {
    val normalized = allergies.trim().lowercase()
    return normalized.isNotBlank() && normalized !in NO_ALLERGY_PHRASES
}

private fun Client.isSensitive(): Boolean {
    val normalized = sensitivity.trim().lowercase()
    return normalized.isNotBlank() &&
        normalized !in LOW_SENSITIVITY_PHRASES &&
        !normalized.startsWith("baja") &&
        !normalized.startsWith("ninguna")
}

fun Client.tagLabels(): List<String> = buildList {
    if (hasAllergy()) add("Alergia")
    if (isSensitive()) add("Sensible")
}

data class ClientListUiState(
    val clients: List<Client> = emptyList(),
    val searchQuery: String = "",
    val selectedFilter: ClientFilter = ClientFilter.TODAS,
) {
    val isEmpty: Boolean get() = clients.isEmpty()
}

@HiltViewModel
class ClientListViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val clientRepository: ClientRepository,
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val selectedFilter = MutableStateFlow(ClientFilter.TODAS)

    val uiState: StateFlow<ClientListUiState> = authRepository.currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else clientRepository.observeClients(user.id)
        }
        .let { clientsFlow ->
            combine(clientsFlow, searchQuery, selectedFilter) { clients, query, filter ->
                val filtered = clients
                    .filter { it.name.contains(query, ignoreCase = true) }
                    .filter { c ->
                        when (filter) {
                            ClientFilter.TODAS -> true
                            ClientFilter.ALERGIAS -> c.hasAllergy()
                            ClientFilter.SENSIBLES -> c.isSensitive()
                        }
                    }
                ClientListUiState(clients = filtered, searchQuery = query, selectedFilter = filter)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ClientListUiState())

    fun onSearchChange(query: String) {
        searchQuery.value = query
    }

    fun onFilterSelected(filter: ClientFilter) {
        selectedFilter.value = filter
    }
}

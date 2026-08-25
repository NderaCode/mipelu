@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.cocido.mipelu.feature.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocido.mipelu.domain.model.Client
import com.cocido.mipelu.domain.model.PlanLimits
import com.cocido.mipelu.domain.repository.AuthRepository
import com.cocido.mipelu.domain.repository.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class NewClientViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val clientRepository: ClientRepository,
) : ViewModel() {

    private val _draft = MutableStateFlow(Client(id = "", ownerUserId = "", name = ""))
    val draft: StateFlow<Client> = _draft.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /** Plan gratuito: hasta [PlanLimits.FREE_CLIENT_LIMIT] clientas. Solo del lado del cliente -
     * el backend todavía no aplica el mismo tope, así que esto no reemplaza esa validación. */
    val isBlockedByLimit: StateFlow<Boolean> = authRepository.currentUser
        .flatMapLatest { user ->
            if (user == null) {
                flowOf(false)
            } else {
                clientRepository.observeClients(user.id)
                    .map { clients -> !user.isPro && clients.size >= PlanLimits.FREE_CLIENT_LIMIT }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun updateField(spec: ClientFieldSpec, value: String) {
        _draft.value = spec.setter(_draft.value, value)
    }

    fun save(onSaved: () -> Unit) {
        val ownerUserId = authRepository.currentUser.value?.id ?: return
        if (isBlockedByLimit.value) {
            _errorMessage.value = "Alcanzaste el límite de clientas del plan gratuito."
            return
        }
        if (_draft.value.name.isBlank()) {
            _errorMessage.value = "Ingresá al menos el nombre de la clienta."
            return
        }
        _errorMessage.value = null
        viewModelScope.launch {
            _isSaving.value = true
            try {
                clientRepository.upsertClient(_draft.value.copy(ownerUserId = ownerUserId))
                onSaved()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "No se pudo guardar la clienta. Probá de nuevo."
            } finally {
                _isSaving.value = false
            }
        }
    }
}

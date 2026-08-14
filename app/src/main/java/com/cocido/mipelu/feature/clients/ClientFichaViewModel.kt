package com.cocido.mipelu.feature.clients

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocido.mipelu.domain.model.Client
import com.cocido.mipelu.domain.repository.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@HiltViewModel
class ClientFichaViewModel @Inject constructor(
    private val clientRepository: ClientRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val clientId: String = savedStateHandle.get<String>("clientId").orEmpty()

    private val _draft = MutableStateFlow<Client?>(null)
    val draft: StateFlow<Client?> = _draft.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        clientRepository.observeClient(clientId)
            .onEach { if (_draft.value == null) _draft.value = it }
            .launchIn(viewModelScope)
    }

    fun updateField(spec: ClientFieldSpec, value: String) {
        _draft.value = _draft.value?.let { spec.setter(it, value) }
    }

    fun save(onSaved: () -> Unit) {
        val current = _draft.value ?: return
        _errorMessage.value = null
        viewModelScope.launch {
            _isSaving.value = true
            try {
                clientRepository.upsertClient(current)
                onSaved()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "No se pudieron guardar los cambios. Probá de nuevo."
            } finally {
                _isSaving.value = false
            }
        }
    }
}

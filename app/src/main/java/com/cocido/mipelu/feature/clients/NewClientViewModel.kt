package com.cocido.mipelu.feature.clients

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocido.mipelu.domain.model.Client
import com.cocido.mipelu.domain.repository.AuthRepository
import com.cocido.mipelu.domain.repository.ClientRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class NewClientViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val clientRepository: ClientRepository,
) : ViewModel() {

    private val _draft = MutableStateFlow(Client(id = "", ownerUserId = "", name = ""))
    val draft: StateFlow<Client> = _draft.asStateFlow()

    fun updateField(spec: ClientFieldSpec, value: String) {
        _draft.value = spec.setter(_draft.value, value)
    }

    fun save(onSaved: () -> Unit) {
        val ownerUserId = authRepository.currentUser.value?.id ?: return
        viewModelScope.launch {
            clientRepository.upsertClient(_draft.value.copy(ownerUserId = ownerUserId))
            onSaved()
        }
    }
}

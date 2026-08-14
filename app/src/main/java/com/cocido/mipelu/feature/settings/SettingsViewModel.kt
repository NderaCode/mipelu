@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.cocido.mipelu.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocido.mipelu.domain.model.UserProfile
import com.cocido.mipelu.domain.repository.AuthRepository
import com.cocido.mipelu.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userProfileRepository: UserProfileRepository,
) : ViewModel() {

    val profile: StateFlow<UserProfile?> = authRepository.currentUser
        .flatMapLatest { user -> if (user == null) flowOf(null) else userProfileRepository.observeProfile(user.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _deleteError = MutableStateFlow<String?>(null)
    val deleteError: StateFlow<String?> = _deleteError.asStateFlow()

    fun updateProfile(name: String, professionalName: String, onSaved: () -> Unit) {
        val current = profile.value ?: return
        if (name.isBlank()) {
            _errorMessage.value = "Ingresá tu nombre."
            return
        }
        _errorMessage.value = null
        _isSaving.value = true
        viewModelScope.launch {
            try {
                userProfileRepository.updateProfile(
                    current.copy(name = name.trim(), professionalName = professionalName.trim()),
                )
                onSaved()
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "No se pudieron guardar los cambios. Probá de nuevo."
            } finally {
                _isSaving.value = false
            }
        }
    }

    fun deleteAccount(onDeleted: () -> Unit) {
        _deleteError.value = null
        viewModelScope.launch {
            authRepository.deleteAccount()
                .onSuccess { onDeleted() }
                .onFailure { _deleteError.value = it.message ?: "No se pudo eliminar la cuenta. Probá de nuevo." }
        }
    }

    fun logout() = authRepository.logout()
}

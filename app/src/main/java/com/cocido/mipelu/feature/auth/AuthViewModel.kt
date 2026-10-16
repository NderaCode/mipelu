package com.cocido.mipelu.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocido.mipelu.core.util.looksLikeEmail
import com.cocido.mipelu.domain.model.UserProfile
import com.cocido.mipelu.domain.repository.AuthRepository
import com.cocido.mipelu.domain.repository.SessionState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

private const val INVALID_EMAIL_MESSAGE = "Revisá el email: parece incompleto."

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    val currentUser: StateFlow<UserProfile?> = authRepository.currentUser

    val sessionState: StateFlow<SessionState> = authRepository.sessionState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        _errorMessage.value = null
        if (!email.looksLikeEmail()) {
            _errorMessage.value = INVALID_EMAIL_MESSAGE
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.login(email, password)
            _isLoading.value = false
            result.onSuccess { onSuccess() }.onFailure { _errorMessage.value = it.message }
        }
    }

    fun signUp(name: String, email: String, password: String, professionalName: String, onSuccess: () -> Unit) {
        _errorMessage.value = null
        if (!email.looksLikeEmail()) {
            _errorMessage.value = INVALID_EMAIL_MESSAGE
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.signUp(name, email, password, professionalName)
            _isLoading.value = false
            result.onSuccess { onSuccess() }.onFailure { _errorMessage.value = it.message }
        }
    }

    fun logout() = authRepository.logout()

    fun clearError() {
        _errorMessage.value = null
    }

    private val _forgotPasswordLoading = MutableStateFlow(false)
    val forgotPasswordLoading: StateFlow<Boolean> = _forgotPasswordLoading.asStateFlow()

    private val _forgotPasswordError = MutableStateFlow<String?>(null)
    val forgotPasswordError: StateFlow<String?> = _forgotPasswordError.asStateFlow()

    private val _forgotPasswordSent = MutableStateFlow(false)
    val forgotPasswordSent: StateFlow<Boolean> = _forgotPasswordSent.asStateFlow()

    fun forgotPassword(email: String) {
        _forgotPasswordError.value = null
        if (!email.looksLikeEmail()) {
            _forgotPasswordError.value = INVALID_EMAIL_MESSAGE
            return
        }
        viewModelScope.launch {
            _forgotPasswordLoading.value = true
            val result = authRepository.forgotPassword(email)
            _forgotPasswordLoading.value = false
            result
                .onSuccess { _forgotPasswordSent.value = true }
                .onFailure { _forgotPasswordError.value = it.message }
        }
    }
}

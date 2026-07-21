package com.cocido.mipelu.feature.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocido.mipelu.domain.model.UserProfile
import com.cocido.mipelu.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
) : ViewModel() {

    val currentUser: StateFlow<UserProfile?> = authRepository.currentUser

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        _errorMessage.value = null
        viewModelScope.launch {
            _isLoading.value = true
            val result = authRepository.login(email, password)
            _isLoading.value = false
            result.onSuccess { onSuccess() }.onFailure { _errorMessage.value = it.message }
        }
    }

    fun signUp(name: String, email: String, password: String, professionalName: String, onSuccess: () -> Unit) {
        _errorMessage.value = null
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
}

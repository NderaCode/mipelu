@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.cocido.mipelu.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocido.mipelu.domain.model.UserProfile
import com.cocido.mipelu.domain.repository.AuthRepository
import com.cocido.mipelu.domain.repository.UserProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    userProfileRepository: UserProfileRepository,
) : ViewModel() {

    val profile: StateFlow<UserProfile?> = authRepository.currentUser
        .flatMapLatest { user -> if (user == null) flowOf(null) else userProfileRepository.observeProfile(user.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun logout() = authRepository.logout()
}

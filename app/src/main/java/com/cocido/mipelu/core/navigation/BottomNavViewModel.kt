package com.cocido.mipelu.core.navigation

import androidx.lifecycle.ViewModel
import com.cocido.mipelu.domain.model.UserProfile
import com.cocido.mipelu.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.StateFlow

@HiltViewModel
class BottomNavViewModel @Inject constructor(
    authRepository: AuthRepository,
) : ViewModel() {
    val currentUser: StateFlow<UserProfile?> = authRepository.currentUser
}

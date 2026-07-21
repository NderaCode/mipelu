@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.cocido.mipelu.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocido.mipelu.domain.model.WorkRecord
import com.cocido.mipelu.domain.repository.AuthRepository
import com.cocido.mipelu.domain.repository.ClientRepository
import com.cocido.mipelu.domain.repository.WorkRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

data class HomeUiState(
    val greetingName: String = "",
    val clientCount: Int = 0,
    val workCount: Int = 0,
    val photoCount: Int = 0,
    val recentWorks: List<WorkRecord> = emptyList(),
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    authRepository: AuthRepository,
    clientRepository: ClientRepository,
    workRecordRepository: WorkRecordRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = authRepository.currentUser
        .flatMapLatest { user ->
            if (user == null) {
                flowOf(HomeUiState())
            } else {
                combine(
                    clientRepository.observeClients(user.id),
                    workRecordRepository.observeWorks(user.id),
                ) { clients, works ->
                    HomeUiState(
                        greetingName = user.name.split(" ").firstOrNull().orEmpty(),
                        clientCount = clients.size,
                        workCount = works.size,
                        photoCount = works.sumOf { it.beforePhotoUrls.size + it.afterPhotoUrls.size },
                        recentWorks = works.take(3),
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())
}

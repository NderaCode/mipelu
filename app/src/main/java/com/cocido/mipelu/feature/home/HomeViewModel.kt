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
import kotlinx.coroutines.launch

data class HomeUiState(
    val greetingName: String = "",
    val clientCount: Int = 0,
    val workCount: Int = 0,
    val photoCount: Int = 0,
    val recentWorks: List<WorkRecord> = emptyList(),
    val isLoading: Boolean = false,
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val clientRepository: ClientRepository,
    private val workRecordRepository: WorkRecordRepository,
) : ViewModel() {

    val uiState: StateFlow<HomeUiState> = authRepository.currentUser
        .flatMapLatest { user ->
            if (user == null) {
                flowOf(HomeUiState())
            } else {
                combine(
                    clientRepository.observeClients(user.id),
                    workRecordRepository.observeWorks(user.id),
                    clientRepository.isLoading,
                    workRecordRepository.isLoading,
                ) { clients, works, isLoadingClients, isLoadingWorks ->
                    HomeUiState(
                        greetingName = user.name.split(" ").firstOrNull().orEmpty(),
                        clientCount = clients.size,
                        workCount = works.size,
                        photoCount = works.sumOf { it.beforePhotoUrls.size + it.afterPhotoUrls.size },
                        recentWorks = works.take(3),
                        isLoading = isLoadingClients || isLoadingWorks,
                    )
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState(isLoading = true))

    fun refresh() {
        val ownerUserId = authRepository.currentUser.value?.id ?: return
        viewModelScope.launch {
            clientRepository.refresh(ownerUserId)
            workRecordRepository.refresh(ownerUserId)
        }
    }
}

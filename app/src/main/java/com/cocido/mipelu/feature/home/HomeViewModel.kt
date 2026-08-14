@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.cocido.mipelu.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocido.mipelu.domain.model.Client
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
    val error: String? = null,
)

// Kotlin's typed combine() overloads stop at 5 flows; clients+works+2 isLoading+2 error would be
// 6. Grouping the "happy path" data into its own combine() and merging the two errors into a
// second one keeps everything typed instead of falling back to the untyped vararg combine().
private data class HomeData(
    val clients: List<Client>,
    val works: List<WorkRecord>,
    val isLoading: Boolean,
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
                val dataFlow = combine(
                    clientRepository.observeClients(user.id),
                    workRecordRepository.observeWorks(user.id),
                    clientRepository.isLoading,
                    workRecordRepository.isLoading,
                ) { clients, works, isLoadingClients, isLoadingWorks ->
                    HomeData(clients, works, isLoadingClients || isLoadingWorks)
                }
                val errorFlow = combine(
                    clientRepository.error,
                    workRecordRepository.error,
                ) { clientsError, worksError -> clientsError ?: worksError }

                combine(dataFlow, errorFlow) { data, error ->
                    HomeUiState(
                        greetingName = user.name.split(" ").firstOrNull().orEmpty(),
                        clientCount = data.clients.size,
                        workCount = data.works.size,
                        photoCount = data.works.sumOf { it.beforePhotoUrls.size + it.afterPhotoUrls.size },
                        recentWorks = data.works.take(3),
                        isLoading = data.isLoading,
                        error = error,
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

@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.cocido.mipelu.feature.workrecords

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocido.mipelu.domain.model.ServiceType
import com.cocido.mipelu.domain.model.WorkRecord
import com.cocido.mipelu.domain.repository.AuthRepository
import com.cocido.mipelu.domain.repository.WorkRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn

data class WorkListUiState(
    val works: List<WorkRecord> = emptyList(),
    val searchQuery: String = "",
    val selectedType: ServiceType? = null,
    val availableTypes: List<ServiceType> = emptyList(),
) {
    val isEmpty: Boolean get() = works.isEmpty()
}

@HiltViewModel
class WorkListViewModel @Inject constructor(
    authRepository: AuthRepository,
    private val workRecordRepository: WorkRecordRepository,
) : ViewModel() {

    private val searchQuery = MutableStateFlow("")
    private val selectedType = MutableStateFlow<ServiceType?>(null)

    val uiState: StateFlow<WorkListUiState> = authRepository.currentUser
        .flatMapLatest { user ->
            if (user == null) flowOf(emptyList()) else workRecordRepository.observeWorks(user.id)
        }
        .let { worksFlow ->
            combine(worksFlow, searchQuery, selectedType) { works, query, type ->
                val filtered = works
                    .filter { type == null || it.serviceType == type }
                    .filter { w ->
                        query.isBlank() ||
                            w.clientName.contains(query, ignoreCase = true) ||
                            w.formula.contains(query, ignoreCase = true) ||
                            w.serviceType.label.contains(query, ignoreCase = true)
                    }
                WorkListUiState(
                    works = filtered,
                    searchQuery = query,
                    selectedType = type,
                    availableTypes = works.map { it.serviceType }.distinct(),
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WorkListUiState())

    fun onSearchChange(query: String) {
        searchQuery.value = query
    }

    fun onTypeSelected(type: ServiceType?) {
        selectedType.value = type
    }
}

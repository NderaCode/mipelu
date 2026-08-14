package com.cocido.mipelu.feature.workrecords

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocido.mipelu.domain.model.WorkRecord
import com.cocido.mipelu.domain.repository.WorkRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class WorkDetailViewModel @Inject constructor(
    private val workRecordRepository: WorkRecordRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val workId: String = savedStateHandle.get<String>("workId").orEmpty()

    val work: StateFlow<WorkRecord?> = workRecordRepository.observeWork(workId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _deleteError = MutableStateFlow<String?>(null)
    val deleteError: StateFlow<String?> = _deleteError.asStateFlow()

    fun deleteWork(onDeleted: () -> Unit) {
        _deleteError.value = null
        viewModelScope.launch {
            try {
                workRecordRepository.deleteWork(workId)
                onDeleted()
            } catch (e: Exception) {
                _deleteError.value = e.message ?: "No se pudo eliminar el trabajo. Probá de nuevo."
            }
        }
    }
}

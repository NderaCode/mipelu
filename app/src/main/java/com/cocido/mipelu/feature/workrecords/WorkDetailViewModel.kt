package com.cocido.mipelu.feature.workrecords

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocido.mipelu.domain.model.WorkRecord
import com.cocido.mipelu.domain.repository.WorkRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class WorkDetailViewModel @Inject constructor(
    workRecordRepository: WorkRecordRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val workId: String = savedStateHandle.get<String>("workId").orEmpty()

    val work: StateFlow<WorkRecord?> = workRecordRepository.observeWork(workId)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}

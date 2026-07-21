@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.cocido.mipelu.feature.workrecords

import android.content.Context
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cocido.mipelu.domain.model.Client
import com.cocido.mipelu.domain.model.PhotoType
import com.cocido.mipelu.domain.model.ServiceType
import com.cocido.mipelu.domain.model.WorkRecord
import com.cocido.mipelu.domain.repository.AuthRepository
import com.cocido.mipelu.domain.repository.ClientRepository
import com.cocido.mipelu.domain.repository.WorkRecordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NewWorkViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val authRepository: AuthRepository,
    private val clientRepository: ClientRepository,
    private val workRecordRepository: WorkRecordRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val initialClientId: String? = savedStateHandle.get<String>("clientId")
    private val duplicateFromWorkId: String? = savedStateHandle.get<String>("duplicateFromWorkId")
    private val editWorkId: String? = savedStateHandle.get<String>("editWorkId")

    private val _draft = MutableStateFlow(
        WorkRecord(
            id = "",
            ownerUserId = "",
            clientId = "",
            clientName = "",
            serviceType = ServiceType.CORTE,
            date = System.currentTimeMillis(),
        ),
    )
    val draft: StateFlow<WorkRecord> = _draft.asStateFlow()

    val clients: StateFlow<List<Client>> = authRepository.currentUser
        .flatMapLatest { user -> if (user == null) flowOf(emptyList()) else clientRepository.observeClients(user.id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        when {
            editWorkId != null -> viewModelScope.launch {
                workRecordRepository.observeWork(editWorkId).firstOrNull()?.let { _draft.value = it }
            }
            else -> {
                initialClientId?.let { id ->
                    viewModelScope.launch {
                        clientRepository.observeClient(id).firstOrNull()?.let { client -> selectClient(client) }
                    }
                }
                duplicateFromWorkId?.let { id ->
                    viewModelScope.launch {
                        workRecordRepository.observeWork(id).firstOrNull()?.let { source ->
                            _draft.update {
                                it.copy(
                                    clientId = source.clientId,
                                    clientName = source.clientName,
                                    serviceType = source.serviceType,
                                    formula = source.formula,
                                    productsUsed = source.productsUsed,
                                    oxidantVolume = source.oxidantVolume,
                                    exposureTime = source.exposureTime,
                                    technique = source.technique,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun selectClient(client: Client) {
        _draft.update { it.copy(clientId = client.id, clientName = client.name) }
    }

    fun selectServiceType(type: ServiceType) {
        _draft.update { it.copy(serviceType = type) }
    }

    fun setDate(millis: Long) {
        _draft.update { it.copy(date = millis) }
    }

    fun updateField(spec: WorkFieldSpec, value: String) {
        _draft.update { spec.setter(it, value) }
    }

    fun setBeforePhoto(url: String) {
        _draft.update { it.copy(beforePhotoUrls = listOf(url)) }
    }

    fun setAfterPhoto(url: String) {
        _draft.update { it.copy(afterPhotoUrls = listOf(url)) }
    }

    fun save(asDraft: Boolean, onSaved: () -> Unit) {
        val ownerUserId = authRepository.currentUser.value?.id ?: return
        val current = _draft.value
        if (current.clientId.isBlank()) return
        viewModelScope.launch {
            // The backend needs a real work record id before it'll accept a photo upload, so
            // the record is saved first and any freshly-picked (still-local) photo is uploaded
            // right after, using the id the server just returned.
            var saved = workRecordRepository.upsertWork(
                current.copy(ownerUserId = ownerUserId, isDraft = asDraft),
            )

            current.beforePhotoUrls.firstOrNull()
                ?.takeIf { it.isNotBlank() && !it.startsWith("http") }
                ?.let { localUri ->
                    readUriBytes(localUri)?.let { (bytes, mimeType) ->
                        saved = workRecordRepository.uploadPhoto(
                            saved.id, PhotoType.before, bytes, mimeType, "before.jpg",
                        )
                    }
                }

            current.afterPhotoUrls.firstOrNull()
                ?.takeIf { it.isNotBlank() && !it.startsWith("http") }
                ?.let { localUri ->
                    readUriBytes(localUri)?.let { (bytes, mimeType) ->
                        saved = workRecordRepository.uploadPhoto(
                            saved.id, PhotoType.after, bytes, mimeType, "after.jpg",
                        )
                    }
                }

            onSaved()
        }
    }

    private fun readUriBytes(uriString: String): Pair<ByteArray, String>? {
        val uri = uriString.toUri()
        val mimeType = context.contentResolver.getType(uri) ?: "image/jpeg"
        val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() } ?: return null
        return bytes to mimeType
    }
}

package com.cocido.mipelu.data.remote

import com.cocido.mipelu.data.local.CachedWork
import com.cocido.mipelu.data.local.WorkRecordLocalStore
import com.cocido.mipelu.data.remote.api.MiPeluApi
import com.cocido.mipelu.data.remote.dto.PhotoDto
import com.cocido.mipelu.data.remote.dto.WorkRecordDetailDto
import com.cocido.mipelu.data.remote.dto.WorkRecordListItemDto
import com.cocido.mipelu.data.remote.mapper.toCached
import com.cocido.mipelu.data.remote.mapper.toCreateRequest
import com.cocido.mipelu.data.remote.mapper.toDetailDto
import com.cocido.mipelu.data.remote.mapper.toDomain
import com.cocido.mipelu.data.remote.mapper.toUpdateRequest
import com.cocido.mipelu.domain.model.PhotoType
import com.cocido.mipelu.domain.model.WorkRecord
import com.cocido.mipelu.domain.repository.WorkRecordRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

private const val WORK_RECORDS_PAGE_LIMIT = 100

/**
 * Room is the source of truth. The list endpoint now returns the full work (formula, products,
 * photos), so refresh() is one paginated pass instead of 1 + N GETs per record.
 */
@Singleton
class RemoteWorkRecordRepository @Inject constructor(
    private val api: MiPeluApi,
    private val local: WorkRecordLocalStore,
) : WorkRecordRepository {

    private val refreshMutex = Mutex()
    private var hasLoadedOnce = false

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    override val error: StateFlow<String?> = _error.asStateFlow()

    @Volatile
    private var lastOwnerUserId: String = ""

    override fun observeWorks(ownerUserId: String): Flow<List<WorkRecord>> {
        lastOwnerUserId = ownerUserId
        return local.observeWorks(ownerUserId).onStart { fetch() }
    }

    override fun observeWorksForClient(clientId: String): Flow<List<WorkRecord>> =
        local.observeWorksForClient(clientId).onStart { fetch() }

    override fun observeWork(workId: String): Flow<WorkRecord?> =
        local.observeWork(workId).onStart { fetch() }

    override suspend fun upsertWork(work: WorkRecord): WorkRecord {
        lastOwnerUserId = work.ownerUserId
        val dto = if (work.id.isBlank()) {
            safeApiCall { api.createWorkRecord(work.toCreateRequest()) }
        } else {
            safeApiCall { api.updateWorkRecord(work.id, work.toUpdateRequest()) }
        }
        persist(dto, work.ownerUserId)
        return dto.toDomain(work.ownerUserId)
    }

    override suspend fun deleteWork(workId: String) {
        safeApiCall { api.deleteWorkRecord(workId) }
        local.delete(workId)
    }

    override suspend fun refresh(ownerUserId: String) {
        lastOwnerUserId = ownerUserId
        fetch(force = true)
    }

    override fun clearCache() {
        hasLoadedOnce = false
        lastOwnerUserId = ""
        runBlocking { local.clear() }
    }

    override suspend fun uploadPhoto(
        workId: String,
        type: PhotoType,
        bytes: ByteArray,
        mimeType: String,
        fileName: String,
    ): WorkRecord {
        val current = local.getCached(workId)
            ?: safeApiCall { api.getWorkRecord(workId) }.let { dto ->
                persist(dto, lastOwnerUserId)
                CachedWork(dto.toDomain(lastOwnerUserId), dto.photos.map { it.toCached() })
            }
        val oldPhoto = current.photos.find { it.type == type.name }

        val filePart = MultipartBody.Part.createFormData(
            "file",
            fileName,
            bytes.toRequestBody(mimeType.toMediaTypeOrNull()),
        )
        val typePart = type.name.toRequestBody("text/plain".toMediaTypeOrNull())
        val newPhoto: PhotoDto = safeApiCall { api.uploadPhoto(workId, filePart, typePart) }

        if (oldPhoto != null) {
            runCatching { safeApiCall { api.deletePhoto(oldPhoto.id) } }
        }

        val updatedPhotos = current.photos.filterNot { it.id == oldPhoto?.id } + newPhoto.toCached()
        val updated = current.work.copy(
            beforePhotoUrls = updatedPhotos.filter { it.type == "before" }.map { it.thumbnailUrl ?: it.url },
            afterPhotoUrls = updatedPhotos.filter { it.type == "after" }.map { it.thumbnailUrl ?: it.url },
        )
        local.upsert(CachedWork(updated, updatedPhotos))
        return updated
    }

    private suspend fun persist(dto: WorkRecordDetailDto, ownerUserId: String) {
        local.upsert(CachedWork(dto.toDomain(ownerUserId), dto.photos.map { it.toCached() }))
    }

    private suspend fun fetch(force: Boolean = false) = refreshMutex.withLock {
        if (hasLoadedOnce && !force) return@withLock
        val ownerUserId = lastOwnerUserId
        val hadData = ownerUserId.isNotBlank() && local.hasAny(ownerUserId)
        if (!hadData) _isLoading.value = true
        try {
            val items = mutableListOf<WorkRecordListItemDto>()
            var page = 1
            while (true) {
                val response = safeApiCall { api.listWorkRecords(page = page, limit = WORK_RECORDS_PAGE_LIMIT) }
                items += response.items
                if (response.items.isEmpty() || items.size >= response.total) break
                page++
            }
            local.replaceAll(
                ownerUserId,
                items.map { item ->
                    val dto = item.toDetailDto()
                    CachedWork(dto.toDomain(ownerUserId), dto.photos.map { it.toCached() })
                },
            )
            hasLoadedOnce = true
            _error.value = null
        } catch (e: MiPeluApiException) {
            _error.value = e.message
        } finally {
            _isLoading.value = false
        }
    }
}

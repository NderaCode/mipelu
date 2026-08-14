package com.cocido.mipelu.data.remote

import com.cocido.mipelu.data.remote.api.MiPeluApi
import com.cocido.mipelu.data.remote.dto.PhotoDto
import com.cocido.mipelu.data.remote.dto.WorkRecordDetailDto
import com.cocido.mipelu.data.remote.dto.WorkRecordListItemDto
import com.cocido.mipelu.data.remote.mapper.toCreateRequest
import com.cocido.mipelu.data.remote.mapper.toDomain
import com.cocido.mipelu.data.remote.mapper.toUpdateRequest
import com.cocido.mipelu.domain.model.PhotoType
import com.cocido.mipelu.domain.model.WorkRecord
import com.cocido.mipelu.domain.repository.WorkRecordRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

// The backend caps `limit` at 100 (see PaginationQueryDto) and 400s above that, so fetch() pages
// through in chunks of this size until `total` is covered.
private const val WORK_RECORDS_PAGE_LIMIT = 100

/**
 * Same N+1 hydration rationale as RemoteClientRepository: GET /work-records only returns
 * card-summary fields, but WorkListViewModel's search filters on formula/products (full-detail
 * fields), so refresh() hydrates every work record's full detail in parallel.
 *
 * Detail DTOs (including each photo's backend `id`, never exposed to the domain model) are
 * cached internally so uploadPhoto() can delete a previous same-type photo without the domain
 * layer needing to know photo ids at all.
 */
@Singleton
class RemoteWorkRecordRepository @Inject constructor(
    private val api: MiPeluApi,
) : WorkRecordRepository {

    private val detailCache = MutableStateFlow<Map<String, WorkRecordDetailDto>>(emptyMap())
    private val refreshMutex = Mutex()
    private var hasLoadedOnce = false

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    override val error: StateFlow<String?> = _error.asStateFlow()

    // This app only ever has one logged-in user per process; ownerUserId is never returned by
    // the backend (ownership is implicit via the JWT), so we just remember the last one we saw.
    @Volatile
    private var lastOwnerUserId: String = ""

    override fun observeWorks(ownerUserId: String): Flow<List<WorkRecord>> =
        detailCache
            .onStart {
                lastOwnerUserId = ownerUserId
                if (!hasLoadedOnce) fetch()
            }
            .map { map -> map.values.map { it.toDomain(ownerUserId) }.sortedByDescending { it.date } }

    override fun observeWorksForClient(clientId: String): Flow<List<WorkRecord>> =
        detailCache
            .onStart { if (!hasLoadedOnce) fetch() }
            .map { map ->
                map.values.filter { it.clientId == clientId }
                    .map { it.toDomain(lastOwnerUserId) }
                    .sortedByDescending { it.date }
            }

    override fun observeWork(workId: String): Flow<WorkRecord?> =
        detailCache
            .onStart { if (!hasLoadedOnce) fetch() }
            .map { map -> map[workId]?.toDomain(lastOwnerUserId) }

    override suspend fun upsertWork(work: WorkRecord): WorkRecord {
        lastOwnerUserId = work.ownerUserId
        val dto = if (work.id.isBlank()) {
            safeApiCall { api.createWorkRecord(work.toCreateRequest()) }
        } else {
            safeApiCall { api.updateWorkRecord(work.id, work.toUpdateRequest()) }
        }
        cache(dto)
        return dto.toDomain(work.ownerUserId)
    }

    override suspend fun deleteWork(workId: String) {
        safeApiCall { api.deleteWorkRecord(workId) }
        detailCache.update { it - workId }
    }

    override suspend fun refresh(ownerUserId: String) {
        lastOwnerUserId = ownerUserId
        fetch(force = true)
    }

    override fun clearCache() {
        hasLoadedOnce = false
        detailCache.value = emptyMap()
        lastOwnerUserId = ""
    }

    override suspend fun uploadPhoto(
        workId: String,
        type: PhotoType,
        bytes: ByteArray,
        mimeType: String,
        fileName: String,
    ): WorkRecord {
        val current = detailCache.value[workId]
            ?: safeApiCall { api.getWorkRecord(workId) }.also { cache(it) }
        val oldPhoto = current.photos.find { it.type == type.name }

        val filePart = MultipartBody.Part.createFormData(
            "file",
            fileName,
            bytes.toRequestBody(mimeType.toMediaTypeOrNull()),
        )
        val typePart = type.name.toRequestBody("text/plain".toMediaTypeOrNull())
        val newPhoto: PhotoDto = safeApiCall { api.uploadPhoto(workId, filePart, typePart) }

        if (oldPhoto != null) {
            // Best-effort cleanup of the replaced photo; a failure here shouldn't fail the upload.
            runCatching { safeApiCall { api.deletePhoto(oldPhoto.id) } }
        }

        val updatedPhotos = current.photos.filterNot { it.id == oldPhoto?.id } + newPhoto
        val updatedDto = current.copy(photos = updatedPhotos)
        cache(updatedDto)
        return updatedDto.toDomain(lastOwnerUserId)
    }

    private fun cache(dto: WorkRecordDetailDto) {
        detailCache.update { it + (dto.id to dto) }
    }

    // No catch here would let a network failure kill this cold flow's onStart{} permanently -
    // every downstream collector (ViewModel combine/stateIn) would crash instead of seeing an
    // error state. hasLoadedOnce stays false on failure so the next subscribe/refresh retries.
    private suspend fun fetch(force: Boolean = false) = refreshMutex.withLock {
        if (hasLoadedOnce && !force) return@withLock
        _isLoading.value = true
        try {
            val summaries = mutableListOf<WorkRecordListItemDto>()
            var page = 1
            while (true) {
                val response = safeApiCall { api.listWorkRecords(page = page, limit = WORK_RECORDS_PAGE_LIMIT) }
                summaries += response.items
                if (response.items.isEmpty() || summaries.size >= response.total) break
                page++
            }
            val details = coroutineScope {
                summaries.map { item -> async { safeApiCall { api.getWorkRecord(item.id) } } }.awaitAll()
            }
            detailCache.value = details.associateBy { it.id }
            hasLoadedOnce = true
            _error.value = null
        } catch (e: MiPeluApiException) {
            _error.value = e.message
        } finally {
            _isLoading.value = false
        }
    }
}

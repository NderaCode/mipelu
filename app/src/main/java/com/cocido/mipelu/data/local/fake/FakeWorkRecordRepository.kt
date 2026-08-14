package com.cocido.mipelu.data.local.fake

import com.cocido.mipelu.domain.model.PhotoType
import com.cocido.mipelu.domain.model.WorkRecord
import com.cocido.mipelu.domain.repository.WorkRecordRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

@Singleton
class FakeWorkRecordRepository @Inject constructor() : WorkRecordRepository {

    private val works = MutableStateFlow(SeedData.works)

    // The in-memory dataset is always already "loaded" - there's nothing to fetch.
    override val isLoading: StateFlow<Boolean> = MutableStateFlow(false).asStateFlow()

    override suspend fun refresh(ownerUserId: String) = Unit

    override fun observeWorks(ownerUserId: String): Flow<List<WorkRecord>> =
        works.map { list ->
            list.filter { it.ownerUserId == ownerUserId }.sortedByDescending { it.date }
        }

    override fun observeWorksForClient(clientId: String): Flow<List<WorkRecord>> =
        works.map { list ->
            list.filter { it.clientId == clientId }.sortedByDescending { it.date }
        }

    override fun observeWork(workId: String): Flow<WorkRecord?> =
        works.map { list -> list.find { it.id == workId } }

    override suspend fun upsertWork(work: WorkRecord): WorkRecord {
        val toSave = if (work.id.isBlank()) {
            work.copy(id = UUID.randomUUID().toString())
        } else {
            work.copy(updatedAt = System.currentTimeMillis())
        }
        works.update { list ->
            if (list.any { it.id == toSave.id }) {
                list.map { if (it.id == toSave.id) toSave else it }
            } else {
                list + toSave
            }
        }
        return toSave
    }

    override suspend fun deleteWork(workId: String) {
        works.update { list -> list.filterNot { it.id == workId } }
    }

    // No real upload without a backend: just stash a fake "uploaded" marker so callers relying
    // on an http(s) URL (vs. a pending local content:// URI) behave consistently in previews/tests.
    override suspend fun uploadPhoto(
        workId: String,
        type: PhotoType,
        bytes: ByteArray,
        mimeType: String,
        fileName: String,
    ): WorkRecord {
        val fakeUrl = "https://fake.local/uploads/$fileName"
        var updated: WorkRecord? = null
        works.update { list ->
            list.map { work ->
                if (work.id != workId) {
                    work
                } else {
                    val next = when (type) {
                        PhotoType.before -> work.copy(beforePhotoUrls = listOf(fakeUrl))
                        PhotoType.after -> work.copy(afterPhotoUrls = listOf(fakeUrl))
                    }
                    updated = next
                    next
                }
            }
        }
        return updated ?: error("WorkRecord $workId not found")
    }

    override fun clearCache() = Unit
}

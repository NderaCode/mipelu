package com.cocido.mipelu.data.local

import com.cocido.mipelu.domain.model.WorkRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/** In-memory [WorkRecordLocalStore] for JVM unit tests that can't open a Room database. */
class InMemoryWorkRecordLocalStore : WorkRecordLocalStore {
    private val works = MutableStateFlow<List<CachedWork>>(emptyList())

    override fun observeWorks(ownerUserId: String): Flow<List<WorkRecord>> =
        works.map { list ->
            list.filter { it.work.ownerUserId == ownerUserId }
                .map { it.work }
                .sortedByDescending { it.date }
        }

    override fun observeWorksForClient(clientId: String): Flow<List<WorkRecord>> =
        works.map { list ->
            list.filter { it.work.clientId == clientId }
                .map { it.work }
                .sortedByDescending { it.date }
        }

    override fun observeWork(workId: String): Flow<WorkRecord?> =
        works.map { list -> list.find { it.work.id == workId }?.work }

    override suspend fun getCached(workId: String): CachedWork? =
        works.value.find { it.work.id == workId }

    override suspend fun hasAny(ownerUserId: String): Boolean =
        works.value.any { it.work.ownerUserId == ownerUserId }

    override suspend fun replaceAll(ownerUserId: String, works: List<CachedWork>) {
        this.works.update { current ->
            current.filterNot { it.work.ownerUserId == ownerUserId } + works
        }
    }

    override suspend fun upsert(cached: CachedWork) {
        works.update { list ->
            if (list.any { it.work.id == cached.work.id }) {
                list.map { if (it.work.id == cached.work.id) cached else it }
            } else {
                list + cached
            }
        }
    }

    override suspend fun delete(workId: String) {
        works.update { list -> list.filterNot { it.work.id == workId } }
    }

    override suspend fun clear() {
        works.value = emptyList()
    }
}

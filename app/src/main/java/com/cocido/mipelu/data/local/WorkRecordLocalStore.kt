package com.cocido.mipelu.data.local

import com.cocido.mipelu.data.local.db.CachedPhoto
import com.cocido.mipelu.domain.model.WorkRecord
import kotlinx.coroutines.flow.Flow

data class CachedWork(
    val work: WorkRecord,
    val photos: List<CachedPhoto>,
)

interface WorkRecordLocalStore {
    fun observeWorks(ownerUserId: String): Flow<List<WorkRecord>>
    fun observeWorksForClient(clientId: String): Flow<List<WorkRecord>>
    fun observeWork(workId: String): Flow<WorkRecord?>
    suspend fun getCached(workId: String): CachedWork?
    suspend fun hasAny(ownerUserId: String): Boolean
    suspend fun replaceAll(ownerUserId: String, works: List<CachedWork>)
    suspend fun upsert(cached: CachedWork)
    suspend fun delete(workId: String)
    suspend fun clear()
}

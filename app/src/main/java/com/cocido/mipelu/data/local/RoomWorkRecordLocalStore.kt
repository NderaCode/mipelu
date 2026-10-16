package com.cocido.mipelu.data.local

import com.cocido.mipelu.data.local.db.WorkRecordDao
import com.cocido.mipelu.data.local.db.decodePhotos
import com.cocido.mipelu.data.local.db.toDomain
import com.cocido.mipelu.data.local.db.toEntity
import com.cocido.mipelu.domain.model.WorkRecord
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class RoomWorkRecordLocalStore @Inject constructor(
    private val dao: WorkRecordDao,
) : WorkRecordLocalStore {

    override fun observeWorks(ownerUserId: String): Flow<List<WorkRecord>> =
        dao.observeAll(ownerUserId).map { rows -> rows.map { it.toDomain() } }

    override fun observeWorksForClient(clientId: String): Flow<List<WorkRecord>> =
        dao.observeForClient(clientId).map { rows -> rows.map { it.toDomain() } }

    override fun observeWork(workId: String): Flow<WorkRecord?> =
        dao.observeById(workId).map { it?.toDomain() }

    override suspend fun getCached(workId: String): CachedWork? {
        val entity = dao.getById(workId) ?: return null
        return CachedWork(entity.toDomain(), entity.decodePhotos())
    }

    override suspend fun hasAny(ownerUserId: String): Boolean = dao.count(ownerUserId) > 0

    override suspend fun replaceAll(ownerUserId: String, works: List<CachedWork>) {
        dao.upsertAll(works.map { it.work.toEntity(it.photos) })
        val keepIds = works.map { it.work.id }
        if (keepIds.isEmpty()) {
            dao.deleteAll()
        } else {
            dao.deleteMissing(ownerUserId, keepIds)
        }
    }

    override suspend fun upsert(cached: CachedWork) {
        dao.upsert(cached.work.toEntity(cached.photos))
    }

    override suspend fun delete(workId: String) {
        dao.deleteById(workId)
    }

    override suspend fun clear() {
        dao.deleteAll()
    }
}

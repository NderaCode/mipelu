package com.cocido.mipelu.data.local

import com.cocido.mipelu.data.local.db.ClientDao
import com.cocido.mipelu.data.local.db.toDomain
import com.cocido.mipelu.data.local.db.toEntity
import com.cocido.mipelu.domain.model.Client
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class RoomClientLocalStore @Inject constructor(
    private val dao: ClientDao,
) : ClientLocalStore {

    override fun observeClients(ownerUserId: String): Flow<List<Client>> =
        dao.observeAll(ownerUserId).map { rows -> rows.map { it.toDomain() } }

    override fun observeClient(clientId: String): Flow<Client?> =
        dao.observeById(clientId).map { it?.toDomain() }

    override suspend fun hasAny(ownerUserId: String): Boolean = dao.count(ownerUserId) > 0

    override suspend fun replaceAll(ownerUserId: String, clients: List<Client>) {
        dao.upsertAll(clients.map { it.toEntity() })
        val keepIds = clients.map { it.id }
        if (keepIds.isEmpty()) {
            dao.deleteAll()
        } else {
            dao.deleteMissing(ownerUserId, keepIds)
        }
    }

    override suspend fun upsert(client: Client) {
        dao.upsert(client.toEntity())
    }

    override suspend fun delete(clientId: String) {
        dao.deleteById(clientId)
    }

    override suspend fun clear() {
        dao.deleteAll()
    }
}

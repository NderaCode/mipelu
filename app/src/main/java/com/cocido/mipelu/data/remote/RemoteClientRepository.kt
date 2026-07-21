package com.cocido.mipelu.data.remote

import com.cocido.mipelu.data.remote.api.MiPeluApi
import com.cocido.mipelu.data.remote.mapper.toDomain
import com.cocido.mipelu.data.remote.mapper.toUpsertRequest
import com.cocido.mipelu.domain.model.Client
import com.cocido.mipelu.domain.repository.ClientRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

// The backend caps `limit` at 100 (see PaginationQueryDto) and 400s above that - a professional
// with more than 100 clients won't see the rest until this repository does real pagination.
private const val CLIENTS_PAGE_LIMIT = 100

/**
 * GET /clients only returns the card-summary fields (name, phone, tags, ...), but the app's
 * allergy/sensitivity filters on ClientListScreen need the full technical sheet. So refresh()
 * fetches the id list first, then hydrates every client's full detail (GET /clients/:id) in
 * parallel - N+1 requests, acceptable at an independent professional's scale (tens of clients,
 * not thousands). If that ever becomes a bottleneck, extend the backend's list DTO instead.
 */
@Singleton
class RemoteClientRepository @Inject constructor(
    private val api: MiPeluApi,
) : ClientRepository {

    private val clients = MutableStateFlow<List<Client>>(emptyList())
    private val refreshMutex = Mutex()
    private var hasLoadedOnce = false

    override fun observeClients(ownerUserId: String): Flow<List<Client>> =
        clients
            .onStart { if (!hasLoadedOnce) refresh(ownerUserId) }
            .map { list -> list.sortedBy { it.name } }

    override fun observeClient(clientId: String): Flow<Client?> =
        clients.map { list -> list.find { it.id == clientId } }

    override suspend fun upsertClient(client: Client): Client {
        val dto = if (client.id.isBlank()) {
            safeApiCall { api.createClient(client.toUpsertRequest()) }
        } else {
            safeApiCall { api.updateClient(client.id, client.toUpsertRequest()) }
        }
        val saved = dto.toDomain(client.ownerUserId)
        clients.update { list ->
            if (list.any { it.id == saved.id }) list.map { if (it.id == saved.id) saved else it }
            else list + saved
        }
        return saved
    }

    override suspend fun deleteClient(clientId: String) {
        safeApiCall { api.deleteClient(clientId) }
        clients.update { list -> list.filterNot { it.id == clientId } }
    }

    override fun clearCache() {
        hasLoadedOnce = false
        clients.value = emptyList()
    }

    private suspend fun refresh(ownerUserId: String) = refreshMutex.withLock {
        if (hasLoadedOnce) return@withLock
        val summary = safeApiCall { api.listClients(limit = CLIENTS_PAGE_LIMIT) }
        val details = coroutineScope {
            summary.items.map { item -> async { safeApiCall { api.getClient(item.id) } } }.awaitAll()
        }
        clients.value = details.map { it.toDomain(ownerUserId) }
        hasLoadedOnce = true
    }
}

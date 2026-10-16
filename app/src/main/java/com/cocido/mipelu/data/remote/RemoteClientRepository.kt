package com.cocido.mipelu.data.remote

import com.cocido.mipelu.data.local.ClientLocalStore
import com.cocido.mipelu.data.remote.api.MiPeluApi
import com.cocido.mipelu.data.remote.dto.ClientListItemDto
import com.cocido.mipelu.data.remote.mapper.toDomain
import com.cocido.mipelu.data.remote.mapper.toUpsertRequest
import com.cocido.mipelu.domain.model.Client
import com.cocido.mipelu.domain.repository.ClientRepository
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

private const val CLIENTS_PAGE_LIMIT = 100

/**
 * Room is the source of truth: observers always read from [local], and network results are
 * written there. That is what makes the client list survive process death and work offline.
 *
 * The list endpoint now returns the ficha fields the Android filters need, so refresh() is one
 * paginated pass instead of 1 + N GETs per client.
 */
@Singleton
class RemoteClientRepository @Inject constructor(
    private val api: MiPeluApi,
    private val local: ClientLocalStore,
) : ClientRepository {

    private val refreshMutex = Mutex()
    private var hasLoadedOnce = false

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    override val error: StateFlow<String?> = _error.asStateFlow()

    override fun observeClients(ownerUserId: String): Flow<List<Client>> =
        local.observeClients(ownerUserId).onStart { fetch(ownerUserId) }

    override fun observeClient(clientId: String): Flow<Client?> =
        local.observeClient(clientId)

    override suspend fun upsertClient(client: Client): Client {
        val dto = if (client.id.isBlank()) {
            safeApiCall { api.createClient(client.toUpsertRequest()) }
        } else {
            safeApiCall { api.updateClient(client.id, client.toUpsertRequest()) }
        }
        val saved = dto.toDomain(client.ownerUserId)
        local.upsert(saved)
        return saved
    }

    override suspend fun deleteClient(clientId: String) {
        safeApiCall { api.deleteClient(clientId) }
        local.delete(clientId)
    }

    override suspend fun refresh(ownerUserId: String) = fetch(ownerUserId, force = true)

    override fun clearCache() {
        hasLoadedOnce = false
        // logout() is a user-initiated barrier: the next login must not race this delete and
        // lose the new account's first write. A DELETE of tens of rows is milliseconds.
        runBlocking { local.clear() }
    }

    private suspend fun fetch(ownerUserId: String, force: Boolean = false) = refreshMutex.withLock {
        if (hasLoadedOnce && !force) return@withLock
        val hadData = local.hasAny(ownerUserId)
        if (!hadData) _isLoading.value = true
        try {
            val items = mutableListOf<ClientListItemDto>()
            var page = 1
            while (true) {
                val response = safeApiCall { api.listClients(page = page, limit = CLIENTS_PAGE_LIMIT) }
                items += response.items
                if (response.items.isEmpty() || items.size >= response.total) break
                page++
            }
            local.replaceAll(ownerUserId, items.map { it.toDomain(ownerUserId) })
            hasLoadedOnce = true
            _error.value = null
        } catch (e: MiPeluApiException) {
            _error.value = e.message
        } finally {
            _isLoading.value = false
        }
    }
}

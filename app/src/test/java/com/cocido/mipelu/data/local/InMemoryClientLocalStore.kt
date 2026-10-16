package com.cocido.mipelu.data.local

import com.cocido.mipelu.domain.model.Client
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

/** In-memory [ClientLocalStore] for JVM unit tests that can't open a Room database. */
class InMemoryClientLocalStore : ClientLocalStore {
    private val clients = MutableStateFlow<List<Client>>(emptyList())

    override fun observeClients(ownerUserId: String): Flow<List<Client>> =
        clients.map { list -> list.filter { it.ownerUserId == ownerUserId }.sortedBy { it.name } }

    override fun observeClient(clientId: String): Flow<Client?> =
        clients.map { list -> list.find { it.id == clientId } }

    override suspend fun hasAny(ownerUserId: String): Boolean =
        clients.value.any { it.ownerUserId == ownerUserId }

    override suspend fun replaceAll(ownerUserId: String, clients: List<Client>) {
        this.clients.update { current ->
            current.filterNot { it.ownerUserId == ownerUserId } + clients
        }
    }

    override suspend fun upsert(client: Client) {
        clients.update { list ->
            if (list.any { it.id == client.id }) list.map { if (it.id == client.id) client else it }
            else list + client
        }
    }

    override suspend fun delete(clientId: String) {
        clients.update { list -> list.filterNot { it.id == clientId } }
    }

    override suspend fun clear() {
        clients.value = emptyList()
    }
}

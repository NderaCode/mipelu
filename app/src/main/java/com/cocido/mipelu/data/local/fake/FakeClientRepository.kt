package com.cocido.mipelu.data.local.fake

import com.cocido.mipelu.domain.model.Client
import com.cocido.mipelu.domain.repository.ClientRepository
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
class FakeClientRepository @Inject constructor() : ClientRepository {

    private val clients = MutableStateFlow(SeedData.clients)

    // The in-memory dataset is always already "loaded" - there's nothing to fetch, so nothing
    // can fail either.
    override val isLoading: StateFlow<Boolean> = MutableStateFlow(false).asStateFlow()
    override val error: StateFlow<String?> = MutableStateFlow(null).asStateFlow()

    override suspend fun refresh(ownerUserId: String) = Unit

    override fun observeClients(ownerUserId: String): Flow<List<Client>> =
        clients.map { list -> list.filter { it.ownerUserId == ownerUserId }.sortedBy { it.name } }

    override fun observeClient(clientId: String): Flow<Client?> =
        clients.map { list -> list.find { it.id == clientId } }

    override suspend fun upsertClient(client: Client): Client {
        val toSave = if (client.id.isBlank()) {
            client.copy(id = UUID.randomUUID().toString())
        } else {
            client.copy(updatedAt = System.currentTimeMillis())
        }
        clients.update { list ->
            if (list.any { it.id == toSave.id }) {
                list.map { if (it.id == toSave.id) toSave else it }
            } else {
                list + toSave
            }
        }
        return toSave
    }

    override suspend fun deleteClient(clientId: String) {
        clients.update { list -> list.filterNot { it.id == clientId } }
    }

    // No-op: the fake repo is one in-memory dataset for the whole process, there's no
    // multi-account cache to invalidate the way there is for the real backend.
    override fun clearCache() = Unit
}

package com.cocido.mipelu.domain.repository

import com.cocido.mipelu.domain.model.Client
import kotlinx.coroutines.flow.Flow

interface ClientRepository {
    fun observeClients(ownerUserId: String): Flow<List<Client>>
    fun observeClient(clientId: String): Flow<Client?>
    suspend fun upsertClient(client: Client): Client
    suspend fun deleteClient(clientId: String)

    /** Drops any cached data so the next observe() re-fetches. Call on logout. */
    fun clearCache()
}

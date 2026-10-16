package com.cocido.mipelu.data.local

import com.cocido.mipelu.domain.model.Client
import kotlinx.coroutines.flow.Flow

/**
 * Local cache of clients. Room is the production implementation; tests inject an in-memory one
 * so they don't need an Android context.
 */
interface ClientLocalStore {
    fun observeClients(ownerUserId: String): Flow<List<Client>>
    fun observeClient(clientId: String): Flow<Client?>
    suspend fun hasAny(ownerUserId: String): Boolean
    suspend fun replaceAll(ownerUserId: String, clients: List<Client>)
    suspend fun upsert(client: Client)
    suspend fun delete(clientId: String)
    suspend fun clear()
}

package com.cocido.mipelu.domain.repository

import com.cocido.mipelu.domain.model.Client
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface ClientRepository {
    fun observeClients(ownerUserId: String): Flow<List<Client>>
    fun observeClient(clientId: String): Flow<Client?>
    suspend fun upsertClient(client: Client): Client
    suspend fun deleteClient(clientId: String)

    /** True while the first fetch of the list is in flight, so screens can tell "still loading"
     * apart from "genuinely empty" and avoid flashing an empty state on cold start. */
    val isLoading: StateFlow<Boolean>

    /** Non-null when the last fetch failed (e.g. offline). Cleared on the next successful fetch. */
    val error: StateFlow<String?>

    /** Re-fetches from the backend even if already loaded once. For pull-to-refresh. */
    suspend fun refresh(ownerUserId: String)

    /** Drops any cached data so the next observe() re-fetches. Call on logout. */
    fun clearCache()
}

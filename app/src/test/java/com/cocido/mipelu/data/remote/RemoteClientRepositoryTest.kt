package com.cocido.mipelu.data.remote

import app.cash.turbine.test
import com.cocido.mipelu.data.remote.api.MiPeluApi
import com.cocido.mipelu.data.remote.dto.ClientDetailDto
import com.cocido.mipelu.data.remote.dto.ClientListItemDto
import com.cocido.mipelu.data.remote.dto.ClientListResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

private fun clientSummary(id: String, name: String = "Cliente $id") =
    ClientListItemDto(id = id, name = name, initials = name.take(1))

private fun clientDetail(id: String, name: String = "Cliente $id") = ClientDetailDto(
    id = id,
    name = name,
    createdAt = "2026-07-20T15:04:33.123Z",
    updatedAt = "2026-07-20T15:04:33.123Z",
)

class RemoteClientRepositoryTest {

    @Test
    fun `network failure on fetch sets error instead of throwing`() = runTest {
        val api = mockk<MiPeluApi>()
        coEvery { api.listClients(any(), any(), any(), any()) } throws IOException("no network")
        val repo = RemoteClientRepository(api)

        // Before the fix, safeApiCall's MiPeluApiException would propagate out of refresh() and
        // crash whatever combine/stateIn was collecting observeClients() - this call must not throw.
        repo.refresh("owner")

        assertNotNull(repo.error.value)
        assertEquals(false, repo.isLoading.value)
    }

    @Test
    fun `successful fetch clears a previous error`() = runTest {
        val api = mockk<MiPeluApi>()
        coEvery { api.listClients(any(), any(), any(), any()) } throws IOException("no network") andThen
            ClientListResponse(items = listOf(clientSummary("1")), total = 1)
        coEvery { api.getClient(any()) } answers { clientDetail(firstArg()) }
        val repo = RemoteClientRepository(api)

        repo.refresh("owner")
        assertNotNull(repo.error.value)

        repo.refresh("owner")
        assertNull(repo.error.value)
    }

    @Test
    fun `fetch pages through multiple requests until total is covered`() = runTest {
        val api = mockk<MiPeluApi>()
        val page1 = ClientListResponse(items = listOf(clientSummary("1"), clientSummary("2")), total = 3)
        val page2 = ClientListResponse(items = listOf(clientSummary("3")), total = 3)
        coEvery { api.listClients(any(), any(), 1, any()) } returns page1
        coEvery { api.listClients(any(), any(), 2, any()) } returns page2
        coEvery { api.getClient(any()) } answers { clientDetail(firstArg()) }
        val repo = RemoteClientRepository(api)

        repo.observeClients("owner").test {
            val result = awaitItem()
            assertEquals(3, result.size)
            assertEquals(setOf("1", "2", "3"), result.map { it.id }.toSet())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { api.listClients(any(), any(), 1, any()) }
        coVerify(exactly = 1) { api.listClients(any(), any(), 2, any()) }
    }

    @Test
    fun `fetch stops paging once total is covered by the first page`() = runTest {
        val api = mockk<MiPeluApi>()
        coEvery { api.listClients(any(), any(), any(), any()) } returns
            ClientListResponse(items = listOf(clientSummary("1")), total = 1)
        coEvery { api.getClient(any()) } answers { clientDetail(firstArg()) }
        val repo = RemoteClientRepository(api)

        repo.refresh("owner")

        coVerify(exactly = 1) { api.listClients(any(), any(), any(), any()) }
        assertTrue(repo.error.value == null)
    }
}

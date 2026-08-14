package com.cocido.mipelu.data.remote

import app.cash.turbine.test
import com.cocido.mipelu.data.remote.api.MiPeluApi
import com.cocido.mipelu.data.remote.dto.WorkRecordDetailDto
import com.cocido.mipelu.data.remote.dto.WorkRecordListItemDto
import com.cocido.mipelu.data.remote.dto.WorkRecordListResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

private fun workSummary(id: String, clientId: String = "client-1") = WorkRecordListItemDto(
    id = id,
    clientId = clientId,
    clientName = "Cliente",
    serviceTypes = listOf("Color"),
    date = "2026-07-20",
    isDraft = false,
)

private fun workDetail(id: String, clientId: String = "client-1") = WorkRecordDetailDto(
    id = id,
    clientId = clientId,
    clientName = "Cliente",
    serviceTypes = listOf("Color"),
    date = "2026-07-20",
    isDraft = false,
    createdAt = "2026-07-20T15:04:33.123Z",
    updatedAt = "2026-07-20T15:04:33.123Z",
)

class RemoteWorkRecordRepositoryTest {

    @Test
    fun `network failure on fetch sets error instead of throwing`() = runTest {
        val api = mockk<MiPeluApi>()
        coEvery { api.listWorkRecords(any(), any(), any(), any(), any()) } throws IOException("no network")
        val repo = RemoteWorkRecordRepository(api)

        // Before the fix, this exception would propagate out of refresh() and crash whatever
        // combine/stateIn was collecting observeWorks() instead of surfacing an error state.
        repo.refresh("owner")

        assertNotNull(repo.error.value)
        assertEquals(false, repo.isLoading.value)
    }

    @Test
    fun `fetch pages through multiple requests until total is covered`() = runTest {
        val api = mockk<MiPeluApi>()
        val page1 = WorkRecordListResponse(items = listOf(workSummary("1"), workSummary("2")), total = 3)
        val page2 = WorkRecordListResponse(items = listOf(workSummary("3")), total = 3)
        coEvery { api.listWorkRecords(any(), any(), any(), 1, any()) } returns page1
        coEvery { api.listWorkRecords(any(), any(), any(), 2, any()) } returns page2
        coEvery { api.getWorkRecord(any()) } answers { workDetail(firstArg()) }
        val repo = RemoteWorkRecordRepository(api)

        repo.observeWorks("owner").test {
            val result = awaitItem()
            assertEquals(3, result.size)
            assertEquals(setOf("1", "2", "3"), result.map { it.id }.toSet())
            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { api.listWorkRecords(any(), any(), any(), 1, any()) }
        coVerify(exactly = 1) { api.listWorkRecords(any(), any(), any(), 2, any()) }
    }
}

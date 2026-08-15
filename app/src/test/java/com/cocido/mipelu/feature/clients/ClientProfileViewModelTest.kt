@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.cocido.mipelu.feature.clients

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.cocido.mipelu.MainDispatcherRule
import com.cocido.mipelu.domain.model.Client
import com.cocido.mipelu.domain.repository.ClientRepository
import com.cocido.mipelu.domain.repository.WorkRecordRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ClientProfileViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val client = Client(id = "client-1", ownerUserId = "owner-1", name = "Ana")

    private fun viewModel(
        clientRepository: ClientRepository = mockk { every { observeClient(any()) } returns flowOf(client) },
        workRecordRepository: WorkRecordRepository = mockk { every { observeWorksForClient(any()) } returns flowOf(emptyList()) },
    ) = ClientProfileViewModel(clientRepository, workRecordRepository, SavedStateHandle(mapOf("clientId" to "client-1")))

    @Test
    fun `lastHairCondition falls back to a placeholder when there are no work records yet`() = runTest {
        val viewModel = viewModel()

        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("Sin registros todavía", state.lastHairCondition)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `deleteClient failure surfaces a fallback message`() = runTest {
        val clientRepository = mockk<ClientRepository>()
        every { clientRepository.observeClient(any()) } returns flowOf(client)
        coEvery { clientRepository.deleteClient(any()) } throws RuntimeException()
        val workRecordRepository = mockk<WorkRecordRepository>()
        every { workRecordRepository.observeWorksForClient(any()) } returns flowOf(emptyList())
        val viewModel = viewModel(clientRepository, workRecordRepository)

        viewModel.deleteClient {}
        advanceUntilIdle()

        assertEquals("No se pudo eliminar la clienta. Probá de nuevo.", viewModel.deleteError.value)
    }
}

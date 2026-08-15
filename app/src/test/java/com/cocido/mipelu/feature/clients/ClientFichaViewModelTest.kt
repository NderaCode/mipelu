@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.cocido.mipelu.feature.clients

import androidx.lifecycle.SavedStateHandle
import com.cocido.mipelu.MainDispatcherRule
import com.cocido.mipelu.domain.model.Client
import com.cocido.mipelu.domain.repository.ClientRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class ClientFichaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val client = Client(id = "client-1", ownerUserId = "owner-1", name = "Ana")

    @Test
    fun `save with no loaded draft is a no-op`() = runTest {
        val clientRepository = mockk<ClientRepository>()
        every { clientRepository.observeClient(any()) } returns flowOf(null)
        val viewModel = ClientFichaViewModel(clientRepository, SavedStateHandle(mapOf("clientId" to "client-1")))

        viewModel.save {}
        advanceUntilIdle()

        coVerify(exactly = 0) { clientRepository.upsertClient(any()) }
    }

    @Test
    fun `save persists the edited draft and surfaces failures`() = runTest {
        val clientRepository = mockk<ClientRepository>()
        every { clientRepository.observeClient("client-1") } returns flowOf(client)
        coEvery { clientRepository.upsertClient(any()) } throws RuntimeException("boom")
        val viewModel = ClientFichaViewModel(clientRepository, SavedStateHandle(mapOf("clientId" to "client-1")))
        advanceUntilIdle() // let the init{} onEach pick up the seeded client

        assertEquals(client, viewModel.draft.value)

        viewModel.save {}
        advanceUntilIdle()

        assertEquals("boom", viewModel.errorMessage.value)
    }
}

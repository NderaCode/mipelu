package com.cocido.mipelu.feature.clients

import app.cash.turbine.test
import com.cocido.mipelu.MainDispatcherRule
import com.cocido.mipelu.domain.model.UserProfile
import com.cocido.mipelu.domain.repository.AuthRepository
import com.cocido.mipelu.domain.repository.ClientRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ClientListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val user = UserProfile(id = "user-1", name = "QA", email = "qa@test.com")

    @Test
    fun `network failure surfaces as an error state instead of crashing`() = runTest {
        val authRepository = mockk<AuthRepository>()
        every { authRepository.currentUser } returns MutableStateFlow(user)

        val clientRepository = mockk<ClientRepository>()
        every { clientRepository.observeClients(any()) } returns flowOf(emptyList())
        every { clientRepository.isLoading } returns MutableStateFlow(false)
        every { clientRepository.error } returns
            MutableStateFlow("No se pudo conectar. Revisá tu conexión a internet.")

        // Before the fix, ClientRepository had no `error` StateFlow at all - a network failure
        // during observeClients()'s onStart{} fetch propagated uncaught into this ViewModel's
        // combine/stateIn instead of ever reaching a collectAsStateWithLifecycle() in the UI.
        val viewModel = ClientListViewModel(authRepository, clientRepository)

        viewModel.uiState.test {
            awaitItem() // seed value from stateIn (isLoading = true) before combine runs
            val state = awaitItem()
            assertNotNull(state.error)
            assertTrue(state.clients.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
}

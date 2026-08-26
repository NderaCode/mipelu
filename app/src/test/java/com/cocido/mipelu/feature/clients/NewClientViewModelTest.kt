@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.cocido.mipelu.feature.clients

import app.cash.turbine.test
import com.cocido.mipelu.MainDispatcherRule
import com.cocido.mipelu.domain.model.Client
import com.cocido.mipelu.domain.model.PlanLimits
import com.cocido.mipelu.domain.model.UserProfile
import com.cocido.mipelu.domain.repository.AuthRepository
import com.cocido.mipelu.domain.repository.ClientRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class NewClientViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val user = UserProfile(id = "owner-1", name = "QA", email = "qa@test.com", plan = "free")
    private val proUser = user.copy(plan = "pro")

    private fun clientsList(count: Int) = List(count) { index ->
        Client(id = "client-$index", ownerUserId = user.id, name = "Clienta $index")
    }

    private fun viewModel(
        clientRepository: ClientRepository,
        currentUser: UserProfile = user,
    ): NewClientViewModel {
        val authRepository = mockk<AuthRepository>()
        every { authRepository.currentUser } returns MutableStateFlow(currentUser)
        return NewClientViewModel(authRepository, clientRepository)
    }

    @Test
    fun `blank name blocks save and shows a validation message without touching the repository`() = runTest {
        val clientRepository = mockk<ClientRepository>()
        val viewModel = viewModel(clientRepository)

        var onSavedCalled = false
        viewModel.save { onSavedCalled = true }
        advanceUntilIdle()

        assertEquals("Ingresá al menos el nombre de la clienta.", viewModel.errorMessage.value)
        assertEquals(false, onSavedCalled)
        coVerify(exactly = 0) { clientRepository.upsertClient(any()) }
    }

    @Test
    fun `successful save clears any previous error and calls onSaved`() = runTest {
        val clientRepository = mockk<ClientRepository>()
        coEvery { clientRepository.upsertClient(any()) } answers { firstArg() }
        val viewModel = viewModel(clientRepository)

        viewModel.updateField(nuevaClientaFields.first { it.label == "Nombre" }, "Ana Pérez")
        var onSavedCalled = false
        viewModel.save { onSavedCalled = true }
        advanceUntilIdle()

        assertNull(viewModel.errorMessage.value)
        assertEquals(true, onSavedCalled)
        assertEquals(false, viewModel.isSaving.value)
    }

    @Test
    fun `repository failure surfaces its message instead of crashing`() = runTest {
        val clientRepository = mockk<ClientRepository>()
        coEvery { clientRepository.upsertClient(any()) } throws
            RuntimeException("No se pudo conectar. Revisá tu conexión a internet.")
        val viewModel = viewModel(clientRepository)

        viewModel.updateField(nuevaClientaFields.first { it.label == "Nombre" }, "Ana Pérez")
        viewModel.save {}
        advanceUntilIdle()

        assertEquals("No se pudo conectar. Revisá tu conexión a internet.", viewModel.errorMessage.value)
        assertEquals(false, viewModel.isSaving.value)
    }

    @Test
    fun `free user under the client limit is not blocked`() = runTest {
        val clientRepository = mockk<ClientRepository>()
        every { clientRepository.observeClients(user.id) } returns
            flowOf(clientsList(PlanLimits.FREE_CLIENT_LIMIT - 1))
        val viewModel = viewModel(clientRepository)

        viewModel.isBlockedByLimit.test {
            assertEquals(false, awaitItem())
        }
    }

    @Test
    fun `free user at the client limit is blocked`() = runTest {
        val clientRepository = mockk<ClientRepository>()
        every { clientRepository.observeClients(user.id) } returns
            flowOf(clientsList(PlanLimits.FREE_CLIENT_LIMIT))
        val viewModel = viewModel(clientRepository)

        viewModel.isBlockedByLimit.test {
            assertEquals(true, awaitItem())
        }
    }

    @Test
    fun `pro user is never blocked, even past the free limit`() = runTest {
        val clientRepository = mockk<ClientRepository>()
        every { clientRepository.observeClients(proUser.id) } returns
            flowOf(clientsList(PlanLimits.FREE_CLIENT_LIMIT + 5))
        val viewModel = viewModel(clientRepository, currentUser = proUser)

        viewModel.isBlockedByLimit.test {
            assertEquals(false, awaitItem())
        }
    }

    @Test
    fun `save refuses to write and surfaces a message once the free limit is reached`() = runTest {
        val clientRepository = mockk<ClientRepository>()
        every { clientRepository.observeClients(user.id) } returns
            flowOf(clientsList(PlanLimits.FREE_CLIENT_LIMIT))
        val viewModel = viewModel(clientRepository)
        viewModel.isBlockedByLimit.test { awaitItem() } // subscribe once so the StateFlow computes

        viewModel.updateField(nuevaClientaFields.first { it.label == "Nombre" }, "Ana Pérez")
        var onSavedCalled = false
        viewModel.save { onSavedCalled = true }
        advanceUntilIdle()

        assertEquals("Alcanzaste el límite de clientas del plan gratuito.", viewModel.errorMessage.value)
        assertEquals(false, onSavedCalled)
        coVerify(exactly = 0) { clientRepository.upsertClient(any()) }
    }
}

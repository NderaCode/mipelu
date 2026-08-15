@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.cocido.mipelu.feature.clients

import com.cocido.mipelu.MainDispatcherRule
import com.cocido.mipelu.domain.model.UserProfile
import com.cocido.mipelu.domain.repository.AuthRepository
import com.cocido.mipelu.domain.repository.ClientRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class NewClientViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val user = UserProfile(id = "owner-1", name = "QA", email = "qa@test.com")

    private fun viewModel(clientRepository: ClientRepository): NewClientViewModel {
        val authRepository = mockk<AuthRepository>()
        every { authRepository.currentUser } returns MutableStateFlow(user)
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
}

@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.cocido.mipelu.feature.workrecords

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import com.cocido.mipelu.MainDispatcherRule
import com.cocido.mipelu.domain.model.Client
import com.cocido.mipelu.domain.model.ServiceType
import com.cocido.mipelu.domain.model.UserProfile
import com.cocido.mipelu.domain.repository.AuthRepository
import com.cocido.mipelu.domain.repository.ClientRepository
import com.cocido.mipelu.domain.repository.WorkRecordRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class NewWorkViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val user = UserProfile(id = "owner-1", name = "QA", email = "qa@test.com")

    private fun viewModel(
        workRecordRepository: WorkRecordRepository,
        clientRepository: ClientRepository = mockk { every { observeClients(any()) } returns emptyFlow() },
    ): NewWorkViewModel {
        val authRepository = mockk<AuthRepository>()
        every { authRepository.currentUser } returns MutableStateFlow(user)
        val context = mockk<Context>(relaxed = true)
        return NewWorkViewModel(context, authRepository, clientRepository, workRecordRepository, SavedStateHandle())
    }

    @Test
    fun `toggleServiceType never leaves the draft with zero service types`() = runTest {
        val viewModel = viewModel(mockk())

        viewModel.toggleServiceType(ServiceType.COLOR)
        assertEquals(listOf(ServiceType.COLOR), viewModel.draft.value.serviceTypes)

        // The only selected type can't be toggled off - the UI requires at least one.
        viewModel.toggleServiceType(ServiceType.COLOR)
        assertEquals(listOf(ServiceType.COLOR), viewModel.draft.value.serviceTypes)

        viewModel.toggleServiceType(ServiceType.CORTE)
        assertEquals(listOf(ServiceType.COLOR, ServiceType.CORTE), viewModel.draft.value.serviceTypes)

        viewModel.toggleServiceType(ServiceType.COLOR)
        assertEquals(listOf(ServiceType.CORTE), viewModel.draft.value.serviceTypes)
    }

    @Test
    fun `save is a no-op without a client or a service type selected`() = runTest {
        val workRecordRepository = mockk<WorkRecordRepository>()
        val viewModel = viewModel(workRecordRepository)

        viewModel.save(asDraft = false) {}
        advanceUntilIdle()

        coVerify(exactly = 0) { workRecordRepository.upsertWork(any()) }
    }

    @Test
    fun `an unparseable price blocks save with a validation message`() = runTest {
        val workRecordRepository = mockk<WorkRecordRepository>()
        val viewModel = viewModel(workRecordRepository)
        val client = Client(id = "client-1", ownerUserId = "owner-1", name = "Ana")

        viewModel.selectClient(client)
        viewModel.toggleServiceType(ServiceType.COLOR)
        viewModel.updateField(resultadoFields.first { it.label == "Precio" }, "abc")
        viewModel.save(asDraft = false) {}
        advanceUntilIdle()

        assertEquals("El precio ingresado no es válido. Ejemplo: 20.000", viewModel.errorMessage.value)
        coVerify(exactly = 0) { workRecordRepository.upsertWork(any()) }
    }

    @Test
    fun `a valid draft saves successfully and calls onSaved`() = runTest {
        val workRecordRepository = mockk<WorkRecordRepository>()
        coEvery { workRecordRepository.upsertWork(any()) } answers { firstArg() }
        val viewModel = viewModel(workRecordRepository)
        val client = Client(id = "client-1", ownerUserId = "owner-1", name = "Ana")

        viewModel.selectClient(client)
        viewModel.toggleServiceType(ServiceType.COLOR)
        var onSavedCalled = false
        viewModel.save(asDraft = false) { onSavedCalled = true }
        advanceUntilIdle()

        assertNull(viewModel.errorMessage.value)
        assertTrue(onSavedCalled)
    }
}

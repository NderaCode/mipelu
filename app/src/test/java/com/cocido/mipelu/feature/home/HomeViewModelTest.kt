package com.cocido.mipelu.feature.home

import app.cash.turbine.test
import com.cocido.mipelu.MainDispatcherRule
import com.cocido.mipelu.domain.model.UserProfile
import com.cocido.mipelu.domain.repository.AuthRepository
import com.cocido.mipelu.domain.repository.ClientRepository
import com.cocido.mipelu.domain.repository.WorkRecordRepository
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val user = UserProfile(id = "user-1", name = "QA Stylist", email = "qa@test.com")

    @Test
    fun `network failure surfaces as an error state instead of crashing`() = runTest {
        val authRepository = mockk<AuthRepository>()
        every { authRepository.currentUser } returns MutableStateFlow(user)

        val clientRepository = mockk<ClientRepository>()
        every { clientRepository.observeClients(any()) } returns flowOf(emptyList())
        every { clientRepository.isLoading } returns MutableStateFlow(false)
        every { clientRepository.error } returns
            MutableStateFlow("No se pudo conectar. Revisá tu conexión a internet.")

        val workRecordRepository = mockk<WorkRecordRepository>()
        every { workRecordRepository.observeWorks(any()) } returns flowOf(emptyList())
        every { workRecordRepository.isLoading } returns MutableStateFlow(false)
        every { workRecordRepository.error } returns MutableStateFlow(null)

        // Before the fix, an uncaught exception from either repository's onStart{} fetch would
        // kill this combine/stateIn instead of ever reaching a collector.
        val viewModel = HomeViewModel(authRepository, clientRepository, workRecordRepository)

        // UnconfinedTestDispatcher runs the combine/stateIn eagerly, so the seed placeholder
        // (isLoading = true) never surfaces as its own emission - the first item already
        // reflects the real (error) state.
        viewModel.uiState.test {
            val state = awaitItem()
            assertNotNull(state.error)
            assertTrue(state.recentWorks.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
}

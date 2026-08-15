package com.cocido.mipelu.feature.workrecords

import app.cash.turbine.test
import com.cocido.mipelu.MainDispatcherRule
import com.cocido.mipelu.domain.model.UserProfile
import com.cocido.mipelu.domain.repository.AuthRepository
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

class WorkListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val user = UserProfile(id = "user-1", name = "QA", email = "qa@test.com")

    @Test
    fun `network failure surfaces as an error state instead of crashing`() = runTest {
        val authRepository = mockk<AuthRepository>()
        every { authRepository.currentUser } returns MutableStateFlow(user)

        val workRecordRepository = mockk<WorkRecordRepository>()
        every { workRecordRepository.observeWorks(any()) } returns flowOf(emptyList())
        every { workRecordRepository.isLoading } returns MutableStateFlow(false)
        every { workRecordRepository.error } returns
            MutableStateFlow("No se pudo conectar. Revisá tu conexión a internet.")

        val viewModel = WorkListViewModel(authRepository, workRecordRepository)

        // UnconfinedTestDispatcher runs the combine/stateIn eagerly, so the seed placeholder
        // (isLoading = true) never surfaces as its own emission - the first item already
        // reflects the real (error) state.
        viewModel.uiState.test {
            val state = awaitItem()
            assertNotNull(state.error)
            assertTrue(state.works.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
}

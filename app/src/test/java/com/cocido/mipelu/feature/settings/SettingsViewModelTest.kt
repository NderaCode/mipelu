@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.cocido.mipelu.feature.settings

import app.cash.turbine.test
import com.cocido.mipelu.MainDispatcherRule
import com.cocido.mipelu.domain.model.UserProfile
import com.cocido.mipelu.domain.repository.AuthRepository
import com.cocido.mipelu.domain.repository.UserProfileRepository
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

class SettingsViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val user = UserProfile(id = "user-1", name = "QA", email = "qa@test.com")

    private fun viewModel(userProfileRepository: UserProfileRepository): SettingsViewModel {
        val authRepository = mockk<AuthRepository>()
        every { authRepository.currentUser } returns MutableStateFlow(user)
        return SettingsViewModel(authRepository, userProfileRepository)
    }

    @Test
    fun `blank name blocks the update without touching the repository`() = runTest {
        val userProfileRepository = mockk<UserProfileRepository>()
        every { userProfileRepository.observeProfile(any()) } returns flowOf(user)
        val viewModel = viewModel(userProfileRepository)

        viewModel.profile.test { awaitItem() } // let the stateIn pick up the seeded profile first

        viewModel.updateProfile(name = "  ", professionalName = "QA", onSaved = {})
        advanceUntilIdle()

        assertEquals("Ingresá tu nombre.", viewModel.errorMessage.value)
        coVerify(exactly = 0) { userProfileRepository.updateProfile(any()) }
    }

    @Test
    fun `deleteAccount failure surfaces a fallback message when the repository gives none`() = runTest {
        val userProfileRepository = mockk<UserProfileRepository>()
        every { userProfileRepository.observeProfile(any()) } returns flowOf(user)
        val authRepository = mockk<AuthRepository>()
        every { authRepository.currentUser } returns MutableStateFlow(user)
        coEvery { authRepository.deleteAccount() } returns Result.failure(RuntimeException())
        val viewModel = SettingsViewModel(authRepository, userProfileRepository)

        viewModel.deleteAccount {}
        advanceUntilIdle()

        assertEquals("No se pudo eliminar la cuenta. Probá de nuevo.", viewModel.deleteError.value)
    }

    @Test
    fun `deleteAccount success calls onDeleted and leaves no error`() = runTest {
        val userProfileRepository = mockk<UserProfileRepository>()
        every { userProfileRepository.observeProfile(any()) } returns flowOf(user)
        val authRepository = mockk<AuthRepository>()
        every { authRepository.currentUser } returns MutableStateFlow(user)
        coEvery { authRepository.deleteAccount() } returns Result.success(Unit)
        val viewModel = SettingsViewModel(authRepository, userProfileRepository)

        var onDeletedCalled = false
        viewModel.deleteAccount { onDeletedCalled = true }
        advanceUntilIdle()

        assertEquals(true, onDeletedCalled)
        assertNull(viewModel.deleteError.value)
    }
}

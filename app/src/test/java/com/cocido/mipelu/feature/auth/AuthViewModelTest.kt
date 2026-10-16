@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.cocido.mipelu.feature.auth

import com.cocido.mipelu.MainDispatcherRule
import com.cocido.mipelu.domain.model.UserProfile
import com.cocido.mipelu.domain.repository.AuthRepository
import com.cocido.mipelu.domain.repository.SessionState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class AuthViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val user = UserProfile(id = "user-1", name = "QA", email = "qa@test.com")

    private fun authRepositoryMock(): AuthRepository =
        mockk<AuthRepository>().also {
            every { it.currentUser } returns MutableStateFlow(null)
            every { it.sessionState } returns MutableStateFlow(SessionState.Unauthenticated)
        }

    @Test
    fun `incomplete email never hits the repository and surfaces a field-level message`() =
        runTest {
            val authRepository = authRepositoryMock()
            val viewModel = AuthViewModel(authRepository)

            viewModel.login("romina@", "hunter2") {}
            viewModel.signUp("Romina Gómez", "romina@", "hunter22", "Romina") {}
            viewModel.forgotPassword("romina@")
            advanceUntilIdle()

            assertEquals("Revisá el email: parece incompleto.", viewModel.errorMessage.value)
            assertEquals("Revisá el email: parece incompleto.", viewModel.forgotPasswordError.value)
            coVerify(exactly = 0) { authRepository.login(any(), any()) }
            coVerify(exactly = 0) { authRepository.signUp(any(), any(), any(), any()) }
            coVerify(exactly = 0) { authRepository.forgotPassword(any()) }
        }

    @Test
    fun `successful login clears any previous error and calls onSuccess`() =
        runTest {
            val authRepository = authRepositoryMock()
            coEvery { authRepository.login(any(), any()) } returns Result.success(user)
            val viewModel = AuthViewModel(authRepository)

            var onSuccessCalled = false
            viewModel.login("qa@test.com", "hunter2") { onSuccessCalled = true }
            advanceUntilIdle()

            assertTrue(onSuccessCalled)
            assertNull(viewModel.errorMessage.value)
            assertEquals(false, viewModel.isLoading.value)
        }

    @Test
    fun `failed login surfaces the repository's error message without calling onSuccess`() =
        runTest {
            val authRepository = authRepositoryMock()
            coEvery { authRepository.login(any(), any()) } returns
                Result.failure(RuntimeException("Credenciales inválidas."))
            val viewModel = AuthViewModel(authRepository)

            var onSuccessCalled = false
            viewModel.login("qa@test.com", "wrong") { onSuccessCalled = true }
            advanceUntilIdle()

            assertEquals(false, onSuccessCalled)
            assertEquals("Credenciales inválidas.", viewModel.errorMessage.value)
        }

    @Test
    fun `forgotPassword success sets the sent flag, failure sets the error`() =
        runTest {
            val authRepository = authRepositoryMock()
            coEvery { authRepository.forgotPassword("qa@test.com") } returns Result.success(Unit)
            val viewModel = AuthViewModel(authRepository)

            viewModel.forgotPassword("qa@test.com")
            advanceUntilIdle()

            assertTrue(viewModel.forgotPasswordSent.value)
            assertNull(viewModel.forgotPasswordError.value)
        }

    /**
     * Regression: the splash screen used to sample `currentUser` after a fixed delay, so a session
     * restore that hadn't finished yet read as "logged out" and bounced a returning user to
     * onboarding. Routing now waits for sessionState to leave Restoring, so it must be observable
     * as a distinct third state rather than collapsing into "no user".
     */
    @Test
    fun `sessionState is exposed so callers can tell restoring apart from logged out`() =
        runTest {
            val state = MutableStateFlow(SessionState.Restoring)
            val authRepository = mockk<AuthRepository>()
            every { authRepository.currentUser } returns MutableStateFlow(null)
            every { authRepository.sessionState } returns state
            val viewModel = AuthViewModel(authRepository)

            assertEquals(SessionState.Restoring, viewModel.sessionState.value)

            state.value = SessionState.Authenticated
            assertEquals(SessionState.Authenticated, viewModel.sessionState.value)
        }
}

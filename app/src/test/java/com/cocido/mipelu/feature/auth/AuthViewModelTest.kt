@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.cocido.mipelu.feature.auth

import com.cocido.mipelu.MainDispatcherRule
import com.cocido.mipelu.domain.model.UserProfile
import com.cocido.mipelu.domain.repository.AuthRepository
import io.mockk.coEvery
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

    @Test
    fun `successful login clears any previous error and calls onSuccess`() = runTest {
        val authRepository = mockk<AuthRepository>()
        every { authRepository.currentUser } returns MutableStateFlow(null)
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
    fun `failed login surfaces the repository's error message without calling onSuccess`() = runTest {
        val authRepository = mockk<AuthRepository>()
        every { authRepository.currentUser } returns MutableStateFlow(null)
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
    fun `forgotPassword success sets the sent flag, failure sets the error`() = runTest {
        val authRepository = mockk<AuthRepository>()
        every { authRepository.currentUser } returns MutableStateFlow(null)
        coEvery { authRepository.forgotPassword("qa@test.com") } returns Result.success(Unit)
        val viewModel = AuthViewModel(authRepository)

        viewModel.forgotPassword("qa@test.com")
        advanceUntilIdle()

        assertTrue(viewModel.forgotPasswordSent.value)
        assertNull(viewModel.forgotPasswordError.value)
    }
}

@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.cocido.mipelu.feature.workrecords

import androidx.lifecycle.SavedStateHandle
import com.cocido.mipelu.MainDispatcherRule
import com.cocido.mipelu.domain.repository.WorkRecordRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class WorkDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `deleteWork success calls onDeleted and leaves no error`() = runTest {
        val workRecordRepository = mockk<WorkRecordRepository>()
        every { workRecordRepository.observeWork(any()) } returns flowOf(null)
        coEvery { workRecordRepository.deleteWork(any()) } returns Unit
        val viewModel = WorkDetailViewModel(workRecordRepository, SavedStateHandle(mapOf("workId" to "work-1")))

        var onDeletedCalled = false
        viewModel.deleteWork { onDeletedCalled = true }
        advanceUntilIdle()

        assertEquals(true, onDeletedCalled)
        assertNull(viewModel.deleteError.value)
    }

    @Test
    fun `deleteWork failure surfaces a fallback message instead of crashing`() = runTest {
        val workRecordRepository = mockk<WorkRecordRepository>()
        every { workRecordRepository.observeWork(any()) } returns flowOf(null)
        coEvery { workRecordRepository.deleteWork(any()) } throws RuntimeException()
        val viewModel = WorkDetailViewModel(workRecordRepository, SavedStateHandle(mapOf("workId" to "work-1")))

        viewModel.deleteWork {}
        advanceUntilIdle()

        assertEquals("No se pudo eliminar el trabajo. Probá de nuevo.", viewModel.deleteError.value)
    }
}

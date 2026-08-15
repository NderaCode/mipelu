package com.cocido.mipelu

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * viewModelScope needs a Main dispatcher installed even under plain JVM unit tests.
 *
 * Deliberately Unconfined, not Standard: a StandardTestDispatcher() here would get its own
 * TestCoroutineScheduler, separate from runTest's - viewModelScope.launch{}/stateIn{} work would
 * sit queued forever unless something calls advanceUntilIdle() on this exact dispatcher instance,
 * which nothing in a test body normally does. Unconfined runs dispatched work immediately instead
 * of queuing it, sidestepping the two-scheduler mismatch entirely.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

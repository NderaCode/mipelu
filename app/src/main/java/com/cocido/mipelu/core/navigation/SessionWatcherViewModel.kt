package com.cocido.mipelu.core.navigation

import androidx.lifecycle.ViewModel
import com.cocido.mipelu.data.remote.auth.SessionExpiredNotifier
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharedFlow

/**
 * Exposes "the session just died" to the navigation layer.
 *
 * Nothing in the UI listened for this before: a refresh token that had expired or been revoked
 * cleared `currentUser` in the repository, but whatever screen the user was on stayed exactly
 * where it was. Every subsequent request 401s, so the app looks broken - empty lists, failed
 * saves - instead of simply asking the user to sign in again.
 */
@HiltViewModel
class SessionWatcherViewModel @Inject constructor(
    sessionExpiredNotifier: SessionExpiredNotifier,
) : ViewModel() {
    val sessionExpired: SharedFlow<Unit> = sessionExpiredNotifier.sessionExpired
}

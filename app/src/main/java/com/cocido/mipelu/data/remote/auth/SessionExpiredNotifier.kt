package com.cocido.mipelu.data.remote.auth

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Bridges [TokenAuthenticator] (an OkHttp [okhttp3.Authenticator], built by NetworkModule) and
 * RemoteAuthRepository (which owns `currentUser` and depends on the authenticated [MiPeluApi])
 * without creating a dependency cycle between the two - both just depend on this standalone
 * singleton instead of on each other.
 */
@Singleton
class SessionExpiredNotifier @Inject constructor() {

    private val _sessionExpired = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpired: SharedFlow<Unit> = _sessionExpired.asSharedFlow()

    fun notifySessionExpired() {
        _sessionExpired.tryEmit(Unit)
    }
}

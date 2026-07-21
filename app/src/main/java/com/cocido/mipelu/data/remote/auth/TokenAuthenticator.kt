package com.cocido.mipelu.data.remote.auth

import com.cocido.mipelu.data.remote.api.MiPeluApi
import com.cocido.mipelu.data.remote.dto.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Response
import okhttp3.Route

/**
 * Handles a 401 by exchanging the stored refresh token for a new access/refresh pair (rotation:
 * the backend revokes the old refresh token on every /auth/refresh call) and retrying the
 * original request once. [refreshApi] is a *separate* Retrofit/OkHttp instance with no
 * AuthInterceptor/Authenticator of its own - reusing the main authenticated client here would
 * recurse back into this same Authenticator.
 *
 * Known limitation: if the refresh itself fails (refresh token expired/revoked), tokens are
 * cleared but nothing currently notifies AuthRepository.currentUser to flip to null - the user
 * stays "logged in" in the UI until they hit another 401 or restart the app. Acceptable for this
 * pass; a follow-up could expose a callback here to force a logout.
 */
class TokenAuthenticator(
    private val tokenStore: TokenStore,
    private val refreshApi: MiPeluApi,
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): okhttp3.Request? {
        val path = response.request.url.encodedPath
        if (path.endsWith("/auth/refresh") || path.endsWith("/auth/login") || path.endsWith("/auth/signup")) {
            return null // a 401 here means bad credentials/invalid token, not an expired session
        }
        if (responseCount(response) >= 2) {
            return null // already retried once, avoid infinite loops
        }

        val refreshToken = tokenStore.refreshToken ?: return null

        val newTokens = try {
            runBlocking { refreshApi.refresh(RefreshTokenRequest(refreshToken)) }
        } catch (e: Exception) {
            tokenStore.clear()
            return null
        }

        tokenStore.saveTokens(newTokens.accessToken, newTokens.refreshToken)

        return response.request.newBuilder()
            .header("Authorization", "Bearer ${newTokens.accessToken}")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}

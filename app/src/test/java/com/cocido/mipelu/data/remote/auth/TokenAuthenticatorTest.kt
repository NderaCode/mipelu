package com.cocido.mipelu.data.remote.auth

import com.cocido.mipelu.data.remote.api.MiPeluApi
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.io.IOException
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertNull
import org.junit.Test

private fun fakeResponse(path: String): Response {
    val request = Request.Builder().url("https://api.example.com$path").build()
    return Response.Builder()
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(401)
        .message("Unauthorized")
        .build()
}

class TokenAuthenticatorTest {

    @Test
    fun `refresh failure clears tokens and notifies session expired`() {
        val tokenStore = mockk<TokenStore>(relaxed = true)
        every { tokenStore.refreshToken } returns "stale-refresh-token"
        val refreshApi = mockk<MiPeluApi>()
        coEvery { refreshApi.refresh(any()) } throws IOException("refresh failed")
        val notifier = mockk<SessionExpiredNotifier>(relaxed = true)
        val authenticator = TokenAuthenticator(tokenStore, refreshApi, notifier)

        // Before the fix, only tokenStore.clear() ran here - currentUser had no way of finding
        // out, so the UI kept showing the user as logged in until the next 401 or app restart.
        val result = authenticator.authenticate(null, fakeResponse("/clients"))

        assertNull(result)
        verify { tokenStore.clear() }
        verify { notifier.notifySessionExpired() }
    }

    @Test
    fun `does not attempt refresh for the auth endpoints themselves`() {
        val tokenStore = mockk<TokenStore>(relaxed = true)
        val refreshApi = mockk<MiPeluApi>()
        val notifier = mockk<SessionExpiredNotifier>(relaxed = true)
        val authenticator = TokenAuthenticator(tokenStore, refreshApi, notifier)

        val result = authenticator.authenticate(null, fakeResponse("/auth/login"))

        assertNull(result)
        verify(exactly = 0) { notifier.notifySessionExpired() }
    }
}

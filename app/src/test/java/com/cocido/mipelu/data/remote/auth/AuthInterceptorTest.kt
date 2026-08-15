package com.cocido.mipelu.data.remote.auth

import io.mockk.every
import io.mockk.mockk
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthInterceptorTest {

    private fun chainFor(path: String): Interceptor.Chain {
        val request = Request.Builder().url("https://api.example.com$path").build()
        val chain = mockk<Interceptor.Chain>()
        every { chain.request() } returns request
        every { chain.proceed(any()) } answers {
            val sentRequest = firstArg<Request>()
            Response.Builder()
                .request(sentRequest)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .build()
        }
        return chain
    }

    @Test
    fun `public auth routes never get an Authorization header, even with a token stored`() {
        val tokenStore = mockk<TokenStore>()
        every { tokenStore.accessToken } returns "some-token"
        val interceptor = AuthInterceptor(tokenStore)

        val response = interceptor.intercept(chainFor("/auth/login"))

        assertNull(response.request.header("Authorization"))
    }

    @Test
    fun `authenticated routes get the Bearer header when a token is stored`() {
        val tokenStore = mockk<TokenStore>()
        every { tokenStore.accessToken } returns "some-token"
        val interceptor = AuthInterceptor(tokenStore)

        val response = interceptor.intercept(chainFor("/clients"))

        assertEquals("Bearer some-token", response.request.header("Authorization"))
    }

    @Test
    fun `authenticated routes get no header when there is no token yet`() {
        val tokenStore = mockk<TokenStore>()
        every { tokenStore.accessToken } returns null
        val interceptor = AuthInterceptor(tokenStore)

        val response = interceptor.intercept(chainFor("/clients"))

        assertNull(response.request.header("Authorization"))
    }

    @Test
    fun `a route that merely ends with a public path suffix is not treated as public`() {
        // Regression test for the endsWith -> exact match hardening: a hypothetical authenticated
        // route ending in the same suffix as a public one must still get its Authorization header.
        val tokenStore = mockk<TokenStore>()
        every { tokenStore.accessToken } returns "some-token"
        val interceptor = AuthInterceptor(tokenStore)

        val response = interceptor.intercept(chainFor("/users/auth/login-history"))

        assertEquals("Bearer some-token", response.request.header("Authorization"))
    }
}

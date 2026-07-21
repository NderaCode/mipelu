package com.cocido.mipelu.data.remote.auth

import okhttp3.Interceptor
import okhttp3.Response

/** Attaches `Authorization: Bearer <accessToken>` to every request except the public auth routes. */
class AuthInterceptor(private val tokenStore: TokenStore) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        val isPublicAuthRoute = PUBLIC_PATHS.any { path.endsWith(it) }
        val accessToken = tokenStore.accessToken

        val authorized = if (!isPublicAuthRoute && accessToken != null) {
            request.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            request
        }

        return chain.proceed(authorized)
    }

    companion object {
        private val PUBLIC_PATHS = listOf(
            "/auth/signup",
            "/auth/login",
            "/auth/refresh",
            "/auth/forgot-password",
            "/auth/reset-password",
        )
    }
}

package com.cocido.mipelu.data.remote

import com.cocido.mipelu.BuildConfig
import com.cocido.mipelu.data.remote.api.MiPeluApi
import com.cocido.mipelu.data.remote.auth.AuthInterceptor
import com.cocido.mipelu.data.remote.auth.SessionExpiredNotifier
import com.cocido.mipelu.data.remote.auth.TokenAuthenticator
import com.cocido.mipelu.data.remote.auth.TokenStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * Two Retrofit/OkHttp stacks on purpose:
 *  - [RefreshClient]: bare (no auth interceptor, no authenticator) - used only by
 *    [TokenAuthenticator] to call POST /auth/refresh. Reusing the authenticated client there
 *    would recurse back into the same authenticator.
 *  - the unqualified [MiPeluApi]: the authenticated one, injected into every repository.
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val CONNECT_TIMEOUT_SECONDS = 15L
    private const val READ_TIMEOUT_SECONDS = 30L

    /** Deliberately the most generous of the three: photo uploads on a salon's mobile uplink
     * legitimately take far longer than any other request this app makes. */
    private const val WRITE_TIMEOUT_SECONDS = 60L

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RefreshClient

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    /**
     * BASIC (method, URL, status, timing) instead of BODY even in debug builds: BODY writes the
     * Authorization header and the raw request bodies of login/signup - i.e. bearer tokens and
     * plaintext passwords - into logcat, which routinely ends up pasted into bug reports. The
     * redactions are belt and braces in case this is ever raised to HEADERS while debugging.
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
            redactHeader("Authorization")
            redactHeader("Cookie")
            redactHeader("Set-Cookie")
        }

    /**
     * Explicit timeouts instead of OkHttp's defaults. A hairdresser using this in a salon is
     * routinely on flaky mobile data, where the failure mode that matters is a request that hangs
     * rather than one that fails outright.
     */
    private fun OkHttpClient.Builder.applyMiPeluTimeouts(): OkHttpClient.Builder =
        connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)

    @Provides
    @Singleton
    @RefreshClient
    fun provideRefreshOkHttpClient(logging: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .applyMiPeluTimeouts()
            .addInterceptor(logging)
            .build()

    @Provides
    @Singleton
    @RefreshClient
    fun provideRefreshRetrofit(@RefreshClient client: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    @RefreshClient
    fun provideRefreshApi(@RefreshClient retrofit: Retrofit): MiPeluApi =
        retrofit.create(MiPeluApi::class.java)

    @Provides
    @Singleton
    fun provideTokenAuthenticator(
        tokenStore: TokenStore,
        @RefreshClient refreshApi: MiPeluApi,
        sessionExpiredNotifier: SessionExpiredNotifier,
    ): TokenAuthenticator = TokenAuthenticator(tokenStore, refreshApi, sessionExpiredNotifier)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor,
        tokenStore: TokenStore,
        authenticator: TokenAuthenticator,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .applyMiPeluTimeouts()
            .addInterceptor(AuthInterceptor(tokenStore))
            .authenticator(authenticator)
            .addInterceptor(logging)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, json: Json): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

    @Provides
    @Singleton
    fun provideMiPeluApi(retrofit: Retrofit): MiPeluApi = retrofit.create(MiPeluApi::class.java)
}

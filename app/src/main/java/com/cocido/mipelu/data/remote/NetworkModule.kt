package com.cocido.mipelu.data.remote

import com.cocido.mipelu.BuildConfig
import com.cocido.mipelu.data.remote.api.MiPeluApi
import com.cocido.mipelu.data.remote.auth.AuthInterceptor
import com.cocido.mipelu.data.remote.auth.TokenAuthenticator
import com.cocido.mipelu.data.remote.auth.TokenStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
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

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class RefreshClient

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }

    @Provides
    @Singleton
    @RefreshClient
    fun provideRefreshOkHttpClient(logging: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
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
    ): TokenAuthenticator = TokenAuthenticator(tokenStore, refreshApi)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        logging: HttpLoggingInterceptor,
        tokenStore: TokenStore,
        authenticator: TokenAuthenticator,
    ): OkHttpClient =
        OkHttpClient.Builder()
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

package com.cocido.mipelu.data.remote.api

import com.cocido.mipelu.data.remote.dto.AuthResponse
import com.cocido.mipelu.data.remote.dto.ClientDetailDto
import com.cocido.mipelu.data.remote.dto.ClientListResponse
import com.cocido.mipelu.data.remote.dto.ClientUpsertRequest
import com.cocido.mipelu.data.remote.dto.CreateWorkRecordRequest
import com.cocido.mipelu.data.remote.dto.DashboardResponse
import com.cocido.mipelu.data.remote.dto.ForgotPasswordRequest
import com.cocido.mipelu.data.remote.dto.LoginRequest
import com.cocido.mipelu.data.remote.dto.PhotoDto
import com.cocido.mipelu.data.remote.dto.RefreshTokenRequest
import com.cocido.mipelu.data.remote.dto.SignupRequest
import com.cocido.mipelu.data.remote.dto.StorageUsageDto
import com.cocido.mipelu.data.remote.dto.UpdateProfileRequest
import com.cocido.mipelu.data.remote.dto.UpdateWorkRecordRequest
import com.cocido.mipelu.data.remote.dto.UserProfileDto
import com.cocido.mipelu.data.remote.dto.WorkRecordDetailDto
import com.cocido.mipelu.data.remote.dto.WorkRecordListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * One Retrofit interface for the whole Mi Pelu API (see api-mipelu/README.md for the full
 * contract). Endpoints that return "no content" (204) use Response<Unit> so the kotlinx
 * serialization converter never tries to parse an empty body; everything else returns its DTO
 * directly and lets Retrofit throw HttpException on non-2xx (handled by ApiCall.safeApiCall).
 */
interface MiPeluApi {

    // --- Auth (public: no Authorization header required, see AuthInterceptor) ---

    @POST("auth/signup")
    suspend fun signup(@Body body: SignupRequest): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): AuthResponse

    @POST("auth/refresh")
    suspend fun refresh(@Body body: RefreshTokenRequest): AuthResponse

    @POST("auth/logout")
    suspend fun logout(@Body body: RefreshTokenRequest): Response<Unit>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body body: ForgotPasswordRequest): Response<Unit>

    // --- Me / account ---

    @GET("me")
    suspend fun getProfile(): UserProfileDto

    @PATCH("me")
    suspend fun updateProfile(@Body body: UpdateProfileRequest): UserProfileDto

    @GET("me/storage-usage")
    suspend fun getStorageUsage(): StorageUsageDto

    @DELETE("me")
    suspend fun deleteAccount(): Response<Unit>

    // --- Clients ---

    @GET("clients")
    suspend fun listClients(
        @Query("search") search: String? = null,
        @Query("filter") filter: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
    ): ClientListResponse

    @POST("clients")
    suspend fun createClient(@Body body: ClientUpsertRequest): ClientDetailDto

    @GET("clients/{id}")
    suspend fun getClient(@Path("id") id: String): ClientDetailDto

    @PATCH("clients/{id}")
    suspend fun updateClient(@Path("id") id: String, @Body body: ClientUpsertRequest): ClientDetailDto

    @DELETE("clients/{id}")
    suspend fun deleteClient(@Path("id") id: String): Response<Unit>

    // --- Work records ---

    @GET("work-records")
    suspend fun listWorkRecords(
        @Query("clientId") clientId: String? = null,
        @Query("search") search: String? = null,
        @Query("filter") filter: String? = null,
        @Query("page") page: Int? = null,
        @Query("limit") limit: Int? = null,
    ): WorkRecordListResponse

    @POST("work-records")
    suspend fun createWorkRecord(@Body body: CreateWorkRecordRequest): WorkRecordDetailDto

    @GET("work-records/{id}")
    suspend fun getWorkRecord(@Path("id") id: String): WorkRecordDetailDto

    @PATCH("work-records/{id}")
    suspend fun updateWorkRecord(
        @Path("id") id: String,
        @Body body: UpdateWorkRecordRequest,
    ): WorkRecordDetailDto

    @DELETE("work-records/{id}")
    suspend fun deleteWorkRecord(@Path("id") id: String): Response<Unit>

    @POST("work-records/{id}/duplicate-formula")
    suspend fun duplicateFormula(@Path("id") id: String): WorkRecordDetailDto

    // --- Photos ---

    @Multipart
    @POST("work-records/{workRecordId}/photos")
    suspend fun uploadPhoto(
        @Path("workRecordId") workRecordId: String,
        @Part file: MultipartBody.Part,
        @Part("type") type: RequestBody,
    ): PhotoDto

    @DELETE("photos/{id}")
    suspend fun deletePhoto(@Path("id") id: String): Response<Unit>

    // --- Dashboard ---

    @GET("dashboard")
    suspend fun getDashboard(): DashboardResponse
}

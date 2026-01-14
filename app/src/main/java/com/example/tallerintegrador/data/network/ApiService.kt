package com.example.tallerintegrador.data.network

import com.example.tallerintegrador.data.model.*
import com.example.tallerintegrador.feature.admin.LogActividad
import com.example.tallerintegrador.feature.admin.Usuario
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ---------- AUTENTICACIÓN ----------

    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @FormUrlEncoded
    @POST("api/register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("password_confirmation") passwordConfirmation: String
    ): LoginResponse

    // ---------- RECUPERAR CONTRASEÑA ----------

    @POST("api/password/send-code")
    suspend fun enviarCodigo(
        @Body request: SolicitarCodigoRequest
    ): Response<CodeResponse>

    @POST("api/password/validate-code")
    suspend fun validarCodigo(
        @Body request: ValidarCodigoRequest
    ): Response<CodeResponse>

    @POST("api/password/reset")
    suspend fun restablecerContrasena(
        @Body request: RestablecerContrasenaRequest
    ): Response<CodeResponse>

    // ---------- PELÍCULAS ----------

    @GET("api/peliculas")
    suspend fun getPeliculas(): List<pelicula>

    @GET("api/peliculas/{id}")
    suspend fun getPeliculaById(@Path("id") peliculaId: Int): pelicula

    // ---------- FAVORITOS ----------

    @GET("api/favoritos")
    suspend fun getFavoritos(
        @Header("Authorization") authHeader: String,
        @Query("profile_id") profileId: String?
    ): List<pelicula>

    @POST("api/favoritos/{peliculaId}")
    suspend fun addFavorito(
        @Header("Authorization") authHeader: String,
        @Path("peliculaId") peliculaId: Int,
        @Query("profile_id") profileId: String?
    ): AddFavoritoResponse

    @DELETE("api/favoritos/{peliculaId}")
    suspend fun removeFavorito(
        @Header("Authorization") authHeader: String,
        @Path("peliculaId") peliculaId: Int,
        @Query("profile_id") profileId: String?
    ): RemoveFavoritoResponse

    @GET("api/favoritos/check/{peliculaId}")
    suspend fun checkFavorito(
        @Header("Authorization") authHeader: String,
        @Path("peliculaId") peliculaId: Int,
        @Query("profile_id") profileId: String?
    ): CheckFavoritoResponse

    // ---------- ADMIN ----------

    @GET("api/admin/dashboard")
    suspend fun getAdminDashboard(
        @Header("Authorization") authHeader: String
    ): AdminDashboardResponse

    @GET("api/admin/users")
    suspend fun getAdminUsers(
        @Header("Authorization") authHeader: String
    ): List<Usuario>

    @DELETE("api/admin/users/{id}")
    suspend fun deleteAdminUser(
        @Header("Authorization") authHeader: String,
        @Path("id") userId: Int
    ): MessageResponse

    @PUT("api/admin/users/{id}")
    suspend fun updateAdminUser(
        @Header("Authorization") authHeader: String,
        @Path("id") userId: Int,
        @Body request: Map<String, String>
    ): MessageResponse

    @GET("api/admin/peliculas")
    suspend fun getAdminPeliculas(
        @Header("Authorization") authHeader: String
    ): List<pelicula>

    @DELETE("api/admin/peliculas/{id}")
    suspend fun deleteAdminPelicula(
        @Header("Authorization") authHeader: String,
        @Path("id") peliculaId: Int
    ): MessageResponse

    @POST("api/admin/peliculas")
    suspend fun createAdminPelicula(
        @Header("Authorization") authHeader: String,
        @Body request: Map<String, String>
    ): pelicula

    @PUT("api/admin/peliculas/{id}")
    suspend fun updateAdminPelicula(
        @Header("Authorization") authHeader: String,
        @Path("id") peliculaId: Int,
        @Body request: Map<String, String>
    ): MessageResponse

    @GET("api/admin/logs")
    suspend fun getAdminLogs(
        @Header("Authorization") authHeader: String
    ): List<LogActividad>

    // ---------- PERFILES ----------

    @Headers("Accept: application/json")
    @GET("api/profiles")
    suspend fun getProfiles(@Header("Authorization") authHeader: String): ProfilesResponse

    @Headers("Accept: application/json")
    @GET("api/profiles/{id}")
    suspend fun getProfile(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): ProfileActionResponse

    @Headers("Accept: application/json")
    @POST("api/profiles")
    suspend fun createProfile(
        @Header("Authorization") authHeader: String,
        @Body request: CreateProfileRequest
    ): ProfileActionResponse

    @Headers("Accept: application/json")
    @PUT("api/profiles/{id}")
    suspend fun updateProfile(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String,
        @Body request: UpdateProfileRequest
    ): ProfileActionResponse

    @Headers("Accept: application/json")
    @FormUrlEncoded
    @POST("api/profiles/{id}")
    suspend fun deleteProfile(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String,
        @Field("_method") method: String = "DELETE"
    ): ProfileActionResponse

    @Headers("Accept: application/json")
    @POST("api/profiles/{id}/select")
    suspend fun selectProfile(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): ProfileActionResponse

    // ---------- SUSCRIPCIONES STRIPE ----------

    @POST("api/stripe/create-checkout-session")
    suspend fun createCheckoutSession(
        @Header("Authorization") authHeader: String,
        @Body request: CreateCheckoutSessionRequest
    ): CheckoutSessionResponse

    @GET("api/subscription/status")
    suspend fun getSubscriptionStatus(
        @Header("Authorization") authHeader: String
    ): SubscriptionStatusResponse

    @POST("api/subscription/verify")
    suspend fun verifySubscription(
        @Header("Authorization") authHeader: String
    ): SubscriptionVerifyResponse

    @POST("api/subscription/activate")
    suspend fun activateSubscription(
        @Header("Authorization") authHeader: String,
        @Body request: ActivateSubscriptionRequest
    ): ActivateSubscriptionResponse

    @GET("api/user")
    suspend fun getUserInfo(
        @Header("Authorization") authHeader: String
    ): User
}

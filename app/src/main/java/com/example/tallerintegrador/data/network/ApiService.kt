package com.example.tallerintegrador.data.network

import com.example.tallerintegrador.data.model.*
import retrofit2.http.*

interface ApiService {

    // ---------- AUTENTICACIÓN ----------

    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): LoginResponse

    // ---------- PELÍCULAS ----------

    @GET("api/peliculas")
    suspend fun getPeliculas(): List<pelicula>

    @GET("api/peliculas/{id}")
    suspend fun getPeliculaById(@Path("id") peliculaId: Int): pelicula

    // ---------- FAVORITOS ----------

    @GET("api/favoritos")
    suspend fun getFavoritos(
        @Header("Authorization") token: String
    ): List<pelicula>

    @POST("api/favoritos/{peliculaId}")
    suspend fun addFavorito(
        @Header("Authorization") token: String,
        @Path("peliculaId") peliculaId: Int
    ): AddFavoritoResponse

    @DELETE("api/favoritos/{peliculaId}")
    suspend fun removeFavorito(
        @Header("Authorization") token: String,
        @Path("peliculaId") peliculaId: Int
    ): RemoveFavoritoResponse

    @GET("api/favoritos/check/{peliculaId}")
    suspend fun checkFavorito(
        @Header("Authorization") authHeader: String,
        @Path("peliculaId") peliculaId: Int
    ): CheckFavoritoResponse
}
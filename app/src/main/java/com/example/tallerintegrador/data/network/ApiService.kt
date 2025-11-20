package com.example.tallerintegrador.data.network

import com.example.tallerintegrador.data.model.*
import retrofit2.http.*

interface ApiService {
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): LoginResponse

    @GET("api/peliculas")
    suspend fun getPeliculas(): List<pelicula>
}

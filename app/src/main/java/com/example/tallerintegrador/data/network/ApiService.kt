package com.example.tallerintegrador.data.network

import com.example.tallerintegrador.data.model.AuthResponse
import com.example.tallerintegrador.data.model.LoginRequest
import com.example.tallerintegrador.data.model.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse
}
package com.example.tallerintegrador.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // ⭐ IMPORTANTE: Cambia según tu entorno
    // Para Android Emulator (ACTUAL - CORRECTO):
    private const val BASE_URL = "http://10.0.2.2:8000/"

    // Para dispositivo físico en la misma red, cambia a tu IP local:
    // private const val BASE_URL = "http://192.168.1.100:8000/"
    // Obtén tu IP con: ipconfig (en Windows) o ifconfig (en Linux/Mac)

    val instance: ApiService by lazy {
        // Creamos un interceptor para loggear el cuerpo de las peticiones y respuestas
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // Creamos un cliente de OkHttp y le añadimos el interceptor
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // Usamos el cliente personalizado
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }
}
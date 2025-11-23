package com.example.tallerintegrador.data.repository

import com.example.tallerintegrador.data.model.pelicula
import com.example.tallerintegrador.data.network.ApiService

class PeliculaRepository(private val apiService: ApiService) {

    /**
     * Obtiene todas las películas desde la API
     */
    suspend fun getPeliculas(): List<pelicula> {
        return apiService.getPeliculas()
    }

    /**
     * ✅ NUEVO: Obtiene una película específica por su ID
     * Esta es la "fuente de verdad" para los detalles de una película
     */
    suspend fun getPeliculaById(peliculaId: Int): pelicula {
        return apiService.getPeliculaById(peliculaId)
    }
}
package com.example.tallerintegrador.data.repository

import com.example.tallerintegrador.data.model.pelicula
import com.example.tallerintegrador.data.network.ApiService

class PeliculaRepository(private val apiService: ApiService) {
    suspend fun getPeliculas(): List<pelicula> {
        return apiService.getPeliculas()
    }
}

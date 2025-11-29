package com.example.tallerintegrador.data.repository

import com.example.tallerintegrador.data.model.pelicula
import com.example.tallerintegrador.data.network.ApiService

class FavoritosRepository(
    private val apiService: ApiService
) {

    /**
     * Obtiene la lista de películas favoritas del usuario.
     * @param token Token puro (sin "Bearer "), por ejemplo: "123abc..."
     */
    suspend fun getFavoritos(token: String): List<pelicula> {
        return apiService.getFavoritos("Bearer $token")
    }

    /**
     * Agrega una película a favoritos.
     */
    suspend fun addFavorito(token: String, peliculaId: Int) {
        apiService.addFavorito("Bearer $token", peliculaId)
    }

    /**
     * Elimina una película de favoritos.
     */
    suspend fun removeFavorito(token: String, peliculaId: Int) {
        apiService.removeFavorito("Bearer $token", peliculaId)
    }

    /**
     * Verifica si una película es favorita.
     */
    suspend fun isFavorito(token: String, peliculaId: Int): Boolean {
        return try {
            val response = apiService.checkFavorito("Bearer $token", peliculaId)
            response.isFavorite
        } catch (_: Exception) {
            false
        }
    }
}
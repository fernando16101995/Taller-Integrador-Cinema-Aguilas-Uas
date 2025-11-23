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
        // ⭐ CAMBIO: Ahora recibimos FavoritosResponse y extraemos la lista
        val response = apiService.getFavoritos("Bearer $token")
        return response.favoritos
    }

    /**
     * Agrega una película a favoritos.
     * ⭐ CAMBIO: Ahora recibimos AddFavoritoResponse
     */
    suspend fun addFavorito(token: String, peliculaId: Int) {
        apiService.addFavorito("Bearer $token", peliculaId)
        // La respuesta indica éxito/error, pero no necesitamos hacer nada especial
    }

    /**
     * Elimina una película de favoritos.
     * ⭐ CAMBIO: Ahora recibimos RemoveFavoritoResponse
     */
    suspend fun removeFavorito(token: String, peliculaId: Int) {
        apiService.removeFavorito("Bearer $token", peliculaId)
        // La respuesta indica éxito/error, pero no necesitamos hacer nada especial
    }

    /**
     * Verifica si una película es favorita.
     * ⭐ CAMBIO: Recibimos CheckFavoritoResponse en lugar de Map<String, Boolean>
     */
    suspend fun isFavorito(token: String, peliculaId: Int): Boolean {
        return try {
            val response = apiService.checkFavorito("Bearer $token", peliculaId)
            response.isFavorite  // ⭐ CAMBIO: Usar el campo del objeto
        } catch (_: Exception) {
            false
        }
    }
}
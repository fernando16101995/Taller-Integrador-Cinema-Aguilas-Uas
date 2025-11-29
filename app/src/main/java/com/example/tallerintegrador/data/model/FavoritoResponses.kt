package com.example.tallerintegrador.data.model

import com.google.gson.annotations.SerializedName

/**
 * Respuesta al agregar un favorito.
 * Estructura: { "message": "Película agregada a favoritos" }
 */
data class AddFavoritoResponse(
    @SerializedName("message")
    val message: String
)

/**
 * Respuesta al eliminar un favorito.
 * Estructura: { "message": "Película eliminada de favoritos" }
 */
data class RemoveFavoritoResponse(
    @SerializedName("message")
    val message: String
)

/**
 * Respuesta al verificar si una película es favorita.
 * Estructura: { "is_favorite": true }
 */
data class CheckFavoritoResponse(
    @SerializedName("is_favorite")
    val isFavorite: Boolean
)
package com.example.tallerintegrador.data.model

import com.google.gson.annotations.SerializedName

/**
 * Respuesta de la API al obtener favoritos.
 * Estructura: { "favoritos": [...], "count": 0 }
 */
data class FavoritosResponse(
    @SerializedName("favoritos")
    val favoritos: List<pelicula>,

    @SerializedName("count")
    val count: Int
)
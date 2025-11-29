package com.example.tallerintegrador.data.model
import com.google.gson.annotations.SerializedName

data class pelicula(

    @SerializedName("title")
    val title: String,
    @SerializedName("description")
    val description: String,
    @SerializedName("poster_url")
    val posterUrl: String,
    @SerializedName("video_url")
    val videoUrl: String,
    @SerializedName("duration_minutes")
    val durationMinutes: Int,
    @SerializedName("genre")
    val genre: String,
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,

    // Campo local para manejar estado en la UI, no viene del JSON
    val esFavorita: Boolean = false
)
    
package com.example.tallerintegrador.data.model

import com.google.gson.annotations.SerializedName

data class pelicula(

    @SerializedName("title")
    val titulo: String,
    @SerializedName("description")
    val sinopsis: String,
    @SerializedName("genre")
    val genero: String,
    @SerializedName("duration_minutes")
    val duracion: Int,
    @SerializedName("poster_url")
    val posterUrl: String,
    @SerializedName("video_url")
    val videoUrl: String
)

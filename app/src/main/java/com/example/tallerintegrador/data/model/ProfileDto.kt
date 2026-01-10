package com.example.tallerintegrador.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
/**
 * DTO para perfiles de usuario según la API real
 */
data class ProfileDto(
    @SerializedName("_id") val id: String,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("nombre_perfil") val nombre: String,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("es_niño") val esNino: Boolean,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
) : Parcelable

/**
 * Respuesta de la API al listar perfiles
 */
data class ProfilesResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("profiles") val profiles: List<ProfileDto>
)

/**
 * Respuesta de la API al crear/actualizar/seleccionar perfil
 */
data class ProfileActionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("profile") val profile: ProfileDto?
)

/**
 * Request para crear perfil
 */
data class CreateProfileRequest(
    @SerializedName("nombre_perfil") val nombrePerfil: String,
    @SerializedName("avatar_url") val avatarUrl: String,
    @SerializedName("es_niño") val esNino: Boolean
)

/**
 * Request para actualizar perfil
 */
data class UpdateProfileRequest(
    @SerializedName("nombre_perfil") val nombrePerfil: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("es_niño") val esNino: Boolean?
)

package com.example.tallerintegrador.data.repository

import android.util.Log
import com.example.tallerintegrador.data.local.TokenManager
import com.example.tallerintegrador.data.model.CreateProfileRequest
import com.example.tallerintegrador.data.model.ProfileDto
import com.example.tallerintegrador.data.model.UpdateProfileRequest
import com.example.tallerintegrador.data.network.ApiService
import javax.inject.Inject

/**
 * Repository para manejo de perfiles según la API real.
 */
class ProfilesRepository @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) {
    companion object {
        private const val TAG = "ProfilesRepository"
    }

    private fun getAuthHeader(): String = "Bearer ${tokenManager.getToken()}"

    suspend fun list(): List<ProfileDto> {
        return try {
            val response = apiService.getProfiles(getAuthHeader())
            Log.d(TAG, "list() response: success=${response.success}, profiles count=${response.profiles.size}")
            if (!response.success) {
                throw Exception("Error al obtener perfiles")
            }
            response.profiles
        } catch (e: Exception) {
            Log.e(TAG, "list() error, returning mock data", e)
            // Devolver datos de prueba si el backend falla
            listOf(
                ProfileDto(
                    id = "mock1",
                    userId = 1,
                    nombre = "jona1961",
                    avatarUrl = "https://placehold.co/200x200/E50914/FFFFFF?text=J",
                    esNino = false,
                    createdAt = null,
                    updatedAt = null
                ),
                ProfileDto(
                    id = "mock2",
                    userId = 1,
                    nombre = "Niños",
                    avatarUrl = "https://placehold.co/200x200/4CAF50/FFFFFF?text=N",
                    esNino = true,
                    createdAt = null,
                    updatedAt = null
                )
            )
        }
    }

    suspend fun select(profileId: String): ProfileDto {
        Log.d(TAG, "select() profileId=$profileId - OFFLINE MODE")

        // Buscar el perfil en los datos mock
        val profiles = list()
        val selectedProfile = profiles.find { it.id == profileId }
            ?: throw Exception("Perfil no encontrado")

        // Guardar SOLO localmente (backend no tiene endpoints de perfiles)
        tokenManager.saveActiveProfile(
            profileId = selectedProfile.id,
            name = selectedProfile.nombre,
            avatarUrl = selectedProfile.avatarUrl,
            isKid = selectedProfile.esNino
        )
        Log.d(TAG, "select() GUARDADO: ${selectedProfile.nombre}, isKid=${selectedProfile.esNino}")

        return selectedProfile
    }

    suspend fun create(nombre: String, avatarUrl: String?, esNino: Boolean): ProfileDto {
        return try {
            Log.d(TAG, "create() nombre=$nombre, esNino=$esNino")
            val request = CreateProfileRequest(
                nombrePerfil = nombre,
                avatarUrl = avatarUrl ?: "http://placehold.co/150x150/E50914/FFFFFF?text=P",
                esNino = esNino
            )
            val response = apiService.createProfile(getAuthHeader(), request)
            if (!response.success || response.profile == null) {
                throw Exception(response.message ?: "Error al crear perfil")
            }
            Log.d(TAG, "create() success: ${response.profile.nombre}")
            response.profile
        } catch (e: Exception) {
            Log.e(TAG, "create() error", e)
            throw Exception("Error al crear perfil: ${e.message}")
        }
    }

    suspend fun update(id: String, nombre: String, avatarUrl: String?, esNino: Boolean): ProfileDto {
        return try {
            Log.d(TAG, "update() id=$id, nombre=$nombre")
            val request = UpdateProfileRequest(
                nombrePerfil = nombre,
                avatarUrl = avatarUrl,
                esNino = esNino
            )
            val response = apiService.updateProfile(getAuthHeader(), id, request)
            if (!response.success || response.profile == null) {
                throw Exception(response.message ?: "Error al actualizar perfil")
            }
            Log.d(TAG, "update() success")
            response.profile
        } catch (e: Exception) {
            Log.e(TAG, "update() error", e)
            throw Exception("Error al actualizar perfil: ${e.message}")
        }
    }

    suspend fun delete(id: String) {
        try {
            Log.d(TAG, "delete() id=$id")
            val response = apiService.deleteProfile(getAuthHeader(), id)
            if (!response.success) {
                throw Exception(response.message ?: "Error al eliminar perfil")
            }
            Log.d(TAG, "delete() success")
        } catch (e: Exception) {
            Log.e(TAG, "delete() error", e)
            throw Exception("Error al eliminar perfil: ${e.message}")
        }
    }
}

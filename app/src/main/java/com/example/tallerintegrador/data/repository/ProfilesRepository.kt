package com.example.tallerintegrador.data.repository

import android.util.Log
import com.example.tallerintegrador.data.local.TokenManager
import com.example.tallerintegrador.data.model.CreateProfileRequest
import com.example.tallerintegrador.data.model.ProfileDto
import com.example.tallerintegrador.data.model.UpdateProfileRequest
import com.example.tallerintegrador.data.network.ApiService
import javax.inject.Inject
import org.json.JSONObject
import retrofit2.HttpException

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
            Log.e(TAG, "list() error", e)
            throw Exception(parseApiError(e))
        }
    }

    suspend fun select(profileId: String): ProfileDto {
        return try {
            Log.d(TAG, "select() profileId=$profileId")
            val response = apiService.selectProfile(getAuthHeader(), profileId)
            if (!response.success || response.profile == null) {
                throw Exception(response.message ?: "Error al seleccionar perfil")
            }
            tokenManager.saveActiveProfile(
                profileId = response.profile.id,
                name = response.profile.nombre,
                avatarUrl = response.profile.avatarUrl,
                isKid = response.profile.esNino
            )
            response.profile
        } catch (e: Exception) {
            Log.e(TAG, "select() error", e)
            throw Exception(parseApiError(e) ?: "Error al seleccionar perfil")
        }
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
            throw Exception(parseApiError(e) ?: "Error al crear perfil")
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
            throw Exception(parseApiError(e) ?: "Error al actualizar perfil")
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
            throw Exception(parseApiError(e) ?: "Error al eliminar perfil")
        }
    }

    private fun parseApiError(throwable: Throwable): String? {
        if (throwable is HttpException) {
            val errorBody = throwable.response()?.errorBody()?.string()
            if (!errorBody.isNullOrBlank()) {
                return try {
                    val json = JSONObject(errorBody)
                    val errors = json.optJSONObject("errors")
                    if (errors != null) {
                        val keys = errors.keys()
                        while (keys.hasNext()) {
                            val key = keys.next()
                            val messages = errors.optJSONArray(key)
                            if (messages != null && messages.length() > 0) {
                                return messages.getString(0)
                            }
                        }
                    }
                    json.optString("message", errorBody)
                } catch (_: Exception) {
                    errorBody
                }
            }
            return "Error ${throwable.code()} ${throwable.message()}"
        }
        return throwable.message
    }
}

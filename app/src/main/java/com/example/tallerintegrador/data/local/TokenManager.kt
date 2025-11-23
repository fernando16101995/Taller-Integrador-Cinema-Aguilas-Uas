package com.example.tallerintegrador.data.local

import android.content.Context
import androidx.core.content.edit

/**
 * Gestor de tokens y sesión de usuario
 * Guarda el token de acceso y la información del usuario en SharedPreferences
 */
class TokenManager(context: Context) {

    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val TOKEN_KEY = "access_token"
        private const val USER_ID_KEY = "user_id"
        private const val USER_NAME_KEY = "user_name"
        private const val USER_EMAIL_KEY = "user_email"
    }

    // Guardar token y datos de usuario
    fun saveAuthData(token: String, userId: Int, userName: String, userEmail: String) {
        prefs.edit {
            putString(TOKEN_KEY, token)
            putInt(USER_ID_KEY, userId)
            putString(USER_NAME_KEY, userName)
            putString(USER_EMAIL_KEY, userEmail)
        }
    }

    // Obtener token
    fun getToken(): String? = prefs.getString(TOKEN_KEY, null)

    // Obtener ID de usuario
    fun getUserId(): Int = prefs.getInt(USER_ID_KEY, -1)

    // Obtener nombre de usuario
    fun getUserName(): String? = prefs.getString(USER_NAME_KEY, null)

    // Obtener email de usuario
    fun getUserEmail(): String? = prefs.getString(USER_EMAIL_KEY, null)

    // Verificar si hay sesión activa
    fun isLoggedIn(): Boolean = getToken() != null

    // Limpiar sesión (logout)
    fun clearSession() {
        prefs.edit {
            clear()
        }
    }
}

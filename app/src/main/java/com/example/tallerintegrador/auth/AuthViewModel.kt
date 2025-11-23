package com.example.tallerintegrador.auth

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tallerintegrador.auth.state.AuthState
import com.example.tallerintegrador.data.local.TokenManager
import com.example.tallerintegrador.data.model.*
import com.example.tallerintegrador.data.network.RetrofitClient
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    val authState = mutableStateOf<AuthState>(AuthState.Idle)

    //  Gestor de tokens
    private val tokenManager = TokenManager(application.applicationContext)

    init {
        // Verificar si hay sesión activa al iniciar
        if (tokenManager.isLoggedIn()) {
            authState.value = AuthState.Success(
                LoginResponse(
                    accessToken = tokenManager.getToken() ?: "",
                    tokenType = "Bearer",
                    user = User(
                        id = tokenManager.getUserId(),
                        name = tokenManager.getUserName() ?: "",
                        email = tokenManager.getUserEmail() ?: ""
                    )
                )
            )
        }
    }

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            authState.value = AuthState.Loading
            try {
                val response = RetrofitClient.instance.login(loginRequest)

                //  GUARDAR TOKEN Y DATOS DE USUARIO
                tokenManager.saveAuthData(
                    token = response.accessToken,
                    userId = response.user.id,
                    userName = response.user.name,
                    userEmail = response.user.email
                )

                authState.value = AuthState.Success(response)
                Log.d("AuthViewModel", "Login exitoso - Token guardado")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login failed", e)
                authState.value = AuthState.Error("Error en el login: ${e.message}")
            }
        }
    }

    fun register(registerRequest: RegisterRequest) {
        viewModelScope.launch {
            authState.value = AuthState.Loading
            try {
                val response = RetrofitClient.instance.register(registerRequest)

                //  GUARDAR TOKEN Y DATOS DE USUARIO
                tokenManager.saveAuthData(
                    token = response.accessToken,
                    userId = response.user.id,
                    userName = response.user.name,
                    userEmail = response.user.email
                )

                authState.value = AuthState.Success(response)
                Log.d("AuthViewModel", "Registro exitoso - Token guardado")
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Registration failed", e)
                authState.value = AuthState.Error("Error en el registro: ${e.message}")
            }
        }
    }

    // Función para cerrar sesión
    fun logout() {
        tokenManager.clearSession()
        authState.value = AuthState.Idle
        Log.d("AuthViewModel", "Sesión cerrada")
    }

    // Obtener token actual
    fun getToken(): String? = tokenManager.getToken()
}
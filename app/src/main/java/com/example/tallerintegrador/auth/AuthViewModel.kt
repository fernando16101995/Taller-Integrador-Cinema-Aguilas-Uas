package com.example.tallerintegrador.auth

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tallerintegrador.auth.state.AuthState
import com.example.tallerintegrador.data.local.TokenManager
import com.example.tallerintegrador.data.model.*
import com.example.tallerintegrador.data.network.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiService: ApiService,
    private val tokenManager: TokenManager
) : ViewModel() {

    val authState = mutableStateOf<AuthState>(AuthState.Idle)

    /* ---------------- LOGIN ---------------- */

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            authState.value = AuthState.Loading
            try {
                val response = apiService.login(request)

                tokenManager.saveAuthData(
                    token = response.accessToken,
                    userId = response.user.id,
                    userName = response.user.name,
                    userEmail = response.user.email
                )

                authState.value = AuthState.AuthSuccess(response)
            } catch (e: Exception) {
                authState.value = AuthState.Error(
                    e.message ?: "Error en el login"
                )
            }
        }
    }

    /* ---------------- REGISTER ---------------- */

    fun register(request: RegisterRequest) {
        viewModelScope.launch {
            authState.value = AuthState.Loading
            try {
                val response = apiService.register(request)

                tokenManager.saveAuthData(
                    token = response.accessToken,
                    userId = response.user.id,
                    userName = response.user.name,
                    userEmail = response.user.email
                )

                authState.value = AuthState.AuthSuccess(response)
            } catch (e: Exception) {
                authState.value = AuthState.Error(
                    e.message ?: "Error en el registro"
                )
            }
        }
    }

    /* ---------- RECUPERAR CONTRASEÑA ---------- */
    fun enviarCodigo(email: String) {
        viewModelScope.launch {
            authState.value = AuthState.Loading
            try {
                val response = apiService.enviarCodigo(
                    SolicitarCodigoRequest(email)
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    authState.value = AuthState.SuccessMessage(
                        response.body()?.message ?: "Código enviado"
                    )
                    Log.d("AuthViewModel", "Código enviado a $email")
                } else {
                    authState.value = AuthState.Error(
                        response.body()?.message ?: "Error al enviar el código"
                    )
                }

            } catch (e: Exception) {
                authState.value = AuthState.Error(
                    e.message ?: "Error al enviar el código"
                )
            }
        }
    }

    fun validarCodigo(email: String, code: String) {
        viewModelScope.launch {
            authState.value = AuthState.Loading
            try {
                val response = apiService.validarCodigo(
                    ValidarCodigoRequest(email, code)
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    authState.value = AuthState.SuccessMessage(
                        response.body()?.message ?: "Código validado"
                    )
                    Log.d("AuthViewModel", "Código validado")
                } else {
                    authState.value = AuthState.Error(
                        response.body()?.message ?: "Código inválido"
                    )
                }

            } catch (e: Exception) {
                authState.value = AuthState.Error(
                    e.message ?: "Código inválido"
                )
            }
        }
    }

    fun restablecerContrasena(
        email: String,
        code: String,
        password: String,
        passwordConfirmation: String
    ) {
        viewModelScope.launch {
            authState.value = AuthState.Loading
            try {
                val response = apiService.restablecerContrasena(
                    RestablecerContrasenaRequest(
                        email = email,
                        code = code,
                        password = password,
                        password_confirmation = passwordConfirmation
                    )
                )

                if (response.isSuccessful && response.body()?.success == true) {
                    authState.value = AuthState.SuccessMessage(
                        response.body()?.message ?: "Contraseña actualizada"
                    )
                    Log.d("AuthViewModel", "Contraseña restablecida")
                } else {
                    authState.value = AuthState.Error(
                        response.body()?.message ?: "Error al cambiar contraseña"
                    )
                }

            } catch (e: Exception) {
                authState.value = AuthState.Error(
                    e.message ?: "No se pudo cambiar la contraseña"
                )
            }
        }
    }

    fun logout() {
        tokenManager.clearSession()
        authState.value = AuthState.Idle
    }
}

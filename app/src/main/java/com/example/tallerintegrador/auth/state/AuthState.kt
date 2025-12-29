package com.example.tallerintegrador.auth.state

import com.example.tallerintegrador.data.model.LoginResponse

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()

    // Para login y register
    data class AuthSuccess(val response: LoginResponse) : AuthState()

    // Para acciones como enviar código, validar, resetear
    data class SuccessMessage(val message: String) : AuthState()

    data class Error(val message: String) : AuthState()
}

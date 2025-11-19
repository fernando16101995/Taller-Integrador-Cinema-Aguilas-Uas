package com.example.tallerintegrador.auth.state

import com.example.tallerintegrador.data.model.LoginResponse

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val authResponse: LoginResponse) : AuthState()
    data class Error(val message: String) : AuthState()
}

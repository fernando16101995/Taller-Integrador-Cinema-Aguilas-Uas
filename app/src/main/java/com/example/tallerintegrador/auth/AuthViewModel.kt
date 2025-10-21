package com.example.tallerintegrador.auth

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tallerintegrador.auth.state.AuthState
import com.example.tallerintegrador.data.model.LoginRequest
import com.example.tallerintegrador.data.model.RegisterRequest
import com.example.tallerintegrador.data.network.RetrofitClient
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    val authState = mutableStateOf<AuthState>(AuthState.Idle)

    fun login(loginRequest: LoginRequest) {
        viewModelScope.launch {
            authState.value = AuthState.Loading
            try {
                val response = RetrofitClient.instance.login(loginRequest)
                authState.value = AuthState.Success(response)
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Login failed", e) // Log de error detallado
                authState.value = AuthState.Error("Error en el login: ${e.message}")
            }
        }
    }

    fun register(registerRequest: RegisterRequest) {
        viewModelScope.launch {
            authState.value = AuthState.Loading
            try {
                val response = RetrofitClient.instance.register(registerRequest)
                authState.value = AuthState.Success(response)
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Registration failed", e) // Log de error detallado
                authState.value = AuthState.Error("Error en el registro: ${e.message}")
            }
        }
    }
}
package com.example.tallerintegrador.data.model

data class SolicitarCodigoRequest(val email: String)
data class ValidarCodigoRequest(val email: String, val code: String)
data class RestablecerContrasenaRequest(
    val email: String,
    val code: String,
    val password: String,
    val password_confirmation: String
)

// Response models
data class CodeResponse(
    val success: Boolean,
    val message: String
)
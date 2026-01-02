package com.example.tallerintegrador.data.model

import com.google.gson.annotations.SerializedName

/**
 * Solicitud para crear una sesión de pago de Stripe
 */
data class CreateCheckoutSessionRequest(
    val plan: String = "monthly"
)

/**
 * Respuesta con la sesión de pago de Stripe
 */
data class CheckoutSessionResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("session_id")
    val sessionId: String?,
    @SerializedName("session_url")
    val sessionUrl: String?,
    @SerializedName("message")
    val message: String?
)

/**
 * Estado de la suscripción del usuario
 */
data class SubscriptionStatusResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("active")
    val active: Boolean,
    @SerializedName("expires_at")
    val expiresAt: String?,
    @SerializedName("plan")
    val plan: String?,
    @SerializedName("message")
    val message: String?
)

/**
 * Respuesta de verificación de suscripción
 */
data class SubscriptionVerifyResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("valid")
    val valid: Boolean,
    @SerializedName("days_remaining")
    val daysRemaining: Int?,
    @SerializedName("message")
    val message: String?
)


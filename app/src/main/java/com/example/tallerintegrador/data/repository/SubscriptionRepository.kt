package com.example.tallerintegrador.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.tallerintegrador.data.model.*
import com.example.tallerintegrador.data.network.ApiService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio para gestionar las suscripciones y pagos con Stripe
 */
@Singleton
class SubscriptionRepository @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    /**
     * Obtiene el token de autenticación almacenado
     */
    private fun getToken(): String? {
        return prefs.getString("token", null)
    }

    /**
     * Crea una sesión de pago con Stripe
     */
    suspend fun createCheckoutSession(plan: String = "monthly"): Result<CheckoutSessionResponse> =
        withContext(Dispatchers.IO) {
            try {
                val token = getToken() ?: return@withContext Result.failure(
                    Exception("No hay token de autenticación")
                )

                val response = apiService.createCheckoutSession(
                    authHeader = "Bearer $token",
                    request = CreateCheckoutSessionRequest(plan)
                )

                if (response.success && response.sessionUrl != null) {
                    Result.success(response)
                } else {
                    Result.failure(Exception(response.message ?: "Error al crear sesión de pago"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Obtiene el estado de la suscripción del usuario
     */
    suspend fun getSubscriptionStatus(): Result<SubscriptionStatusResponse> =
        withContext(Dispatchers.IO) {
            try {
                val token = getToken() ?: return@withContext Result.failure(
                    Exception("No hay token de autenticación")
                )

                val response = apiService.getSubscriptionStatus(
                    authHeader = "Bearer $token"
                )

                if (response.success) {
                    Result.success(response)
                } else {
                    Result.failure(Exception(response.message ?: "Error al obtener estado"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Verifica si la suscripción del usuario es válida
     */
    suspend fun verifySubscription(): Result<SubscriptionVerifyResponse> =
        withContext(Dispatchers.IO) {
            try {
                val token = getToken() ?: return@withContext Result.failure(
                    Exception("No hay token de autenticación")
                )

                val response = apiService.verifySubscription(
                    authHeader = "Bearer $token"
                )

                if (response.success) {
                    Result.success(response)
                } else {
                    Result.failure(Exception(response.message ?: "Error al verificar suscripción"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Obtiene información actualizada del usuario incluyendo estado de suscripción
     */
    suspend fun getUserInfo(): Result<User> = withContext(Dispatchers.IO) {
        try {
            val token = getToken() ?: return@withContext Result.failure(
                Exception("No hay token de autenticación")
            )

            val user = apiService.getUserInfo(authHeader = "Bearer $token")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Verifica si el usuario tiene una suscripción activa
     */
    suspend fun hasActiveSubscription(): Boolean = withContext(Dispatchers.IO) {
        try {
            val token = getToken() ?: return@withContext false
            val user = apiService.getUserInfo(authHeader = "Bearer $token")
            user.suscripcionActiva
        } catch (e: Exception) {
            false
        }
    }
}


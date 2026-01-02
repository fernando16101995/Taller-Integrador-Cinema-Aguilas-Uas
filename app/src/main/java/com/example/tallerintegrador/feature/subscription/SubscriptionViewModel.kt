package com.example.tallerintegrador.feature.subscription

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tallerintegrador.data.model.SubscriptionStatusResponse
import com.example.tallerintegrador.data.repository.SubscriptionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para gestionar el estado de las suscripciones
 */
@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val repository: SubscriptionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SubscriptionUiState())
    val uiState: StateFlow<SubscriptionUiState> = _uiState.asStateFlow()

    /**
     * Estado de la interfaz de suscripción
     */
    data class SubscriptionUiState(
        val isLoading: Boolean = false,
        val hasActiveSubscription: Boolean = false,
        val subscriptionStatus: SubscriptionStatusResponse? = null,
        val checkoutUrl: String? = null,
        val error: String? = null,
        val daysRemaining: Int? = null
    )

    init {
        checkSubscriptionStatus()
    }

    /**
     * Verifica el estado de la suscripción del usuario
     */
    fun checkSubscriptionStatus() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                // Primero verificamos si tiene suscripción activa
                val hasActive = repository.hasActiveSubscription()

                // Luego obtenemos el estado completo
                val statusResult = repository.getSubscriptionStatus()

                statusResult.fold(
                    onSuccess = { status ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            hasActiveSubscription = hasActive && status.active,
                            subscriptionStatus = status,
                            error = null
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            hasActiveSubscription = hasActive,
                            error = error.message ?: "Error al verificar suscripción"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error desconocido"
                )
            }
        }
    }

    /**
     * Crea una sesión de pago con Stripe
     */
    fun createCheckoutSession(onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = repository.createCheckoutSession("monthly")

            result.fold(
                onSuccess = { response ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        checkoutUrl = response.sessionUrl
                    )
                    response.sessionUrl?.let { onSuccess(it) }
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error al crear sesión de pago"
                    )
                }
            )
        }
    }

    /**
     * Verifica la validez de la suscripción y días restantes
     */
    fun verifySubscription() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            val result = repository.verifySubscription()

            result.fold(
                onSuccess = { verify ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        hasActiveSubscription = verify.valid,
                        daysRemaining = verify.daysRemaining,
                        error = null
                    )
                },
                onFailure = { error ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error al verificar suscripción"
                    )
                }
            )
        }
    }

    /**
     * Limpia el error del estado
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    /**
     * Refresca el estado de la suscripción
     */
    fun refresh() {
        checkSubscriptionStatus()
        verifySubscription()
    }
}


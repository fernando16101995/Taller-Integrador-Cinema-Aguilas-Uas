package com.example.tallerintegrador.feature.profiles

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.request.SuccessResult
import com.example.tallerintegrador.auth.state.AuthState
import com.example.tallerintegrador.data.model.ProfileDto
import com.example.tallerintegrador.data.repository.ProfilesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la gestión de perfiles de usuario
 * Maneja la carga, creación, actualización y eliminación de perfiles
 * Utiliza inyección de dependencias con Hilt
 */
@HiltViewModel
class ProfilesViewModel @Inject constructor(
    private val repository: ProfilesRepository
) : ViewModel() {

    companion object {
        private const val TAG = "ProfilesViewModel"
    }

    /**
     * Estado de la UI que contiene:
     * - loading: indica si se está cargando información
     * - profiles: lista de perfiles disponibles
     * - error: mensaje de error si ocurre algún problema
     */
    data class UiState(
        val loading: Boolean = false,
        val profiles: List<ProfileDto> = emptyList(),
        val error: String? = null
    )

    // Estado mutable privado
    private val _uiState = MutableStateFlow(UiState())
    // Estado observable público
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // Flujo de eventos para notificar cuando se guarda un perfil
    private val _saved = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val saved = _saved.asSharedFlow()

    /**
     * Carga la lista de perfiles desde el repositorio
     * Actualiza el estado de la UI según el resultado (éxito o error)
     */
    fun load() = viewModelScope.launch {
        Log.d(TAG, "load() started")
        _uiState.value = _uiState.value.copy(loading = true, error = null)
        runCatching { repository.list() }
            .onSuccess {
                Log.d(TAG, "load() success: ${it.size} profiles")
                _uiState.value = _uiState.value.copy(loading = false, profiles = it)
            }
            .onFailure {
                Log.e(TAG, "load() error", it)
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = it.message ?: "Error al cargar perfiles"
                )
            }
    }

    /**
     * Selecciona un perfil y ejecuta el callback de navegación
     * Modo simplificado: navega directamente sin operaciones complejas
     *
     * @param profileId ID del perfil seleccionado
     * @param onDone Callback que se ejecuta al completar la selección
     */
    fun select(profileId: String?, onDone: () -> Unit) {
        Log.d(TAG, "select() profileId=$profileId")

        // 1. Validación de seguridad para el ID nulo
        if (profileId == null) {
            _uiState.value = _uiState.value.copy(error = "ID de perfil inválido (nulo)")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, error = null)
            try {
                // 2. Llamada al repositorio (el ID ya es seguro aquí)
                repository.select(profileId)

                _uiState.value = _uiState.value.copy(loading = false)

                // 3. ÉXITO: Ejecutamos el callback de navegación
                onDone()

            } catch (e: Exception) {
                Log.e(TAG, "Error seleccionando perfil", e)
                _uiState.value = _uiState.value.copy(
                    loading = false,
                    error = e.message ?: "No se pudo seleccionar el perfil"
                )
                // IMPORTANTE: Aquí NO se llama a onDone(), por lo que el usuario se queda en la pantalla de perfiles viendo el error.
            }
        }
    }

    /**
     * Crea un nuevo perfil
     *
     * @param nombre Nombre del perfil
     * @param avatarUrl URL del avatar (opcional)
     * @param esNino Indica si es un perfil para niños
     */
    fun create(nombre: String, avatarUrl: String?, esNino: Boolean) = viewModelScope.launch {
        runCatching { repository.create(nombre, avatarUrl, esNino) }
            .onSuccess {
                _uiState.value = _uiState.value.copy(profiles = _uiState.value.profiles + it)
                _saved.tryEmit(Unit)
            }
            .onFailure {
                _uiState.value = _uiState.value.copy(error = it.message ?: "No se pudo crear el perfil")
            }
    }

    /**
     * Actualiza un perfil existente
     *
     * @param id ID del perfil a actualizar
     * @param nombre Nuevo nombre del perfil
     * @param avatarUrl Nueva URL del avatar (opcional)
     * @param esNino Indica si es un perfil para niños
     */
    fun update(id: String, nombre: String, avatarUrl: String?, esNino: Boolean) = viewModelScope.launch {
        runCatching { repository.update(id, nombre, avatarUrl, esNino) }
            .onSuccess { updated ->
                _uiState.value = _uiState.value.copy(
                    profiles = _uiState.value.profiles.map { if (it.id == id) updated else it }
                )
                _saved.tryEmit(Unit)
            }
            .onFailure {
                _uiState.value = _uiState.value.copy(error = it.message ?: "No se pudo actualizar el perfil")
            }
    }

    /**
     * Elimina un perfil existente
     *
     * @param id ID del perfil a eliminar
     */
    fun delete(id: String) = viewModelScope.launch {
        runCatching { repository.delete(id) }
            .onSuccess {
                _uiState.value = _uiState.value.copy(
                    profiles = _uiState.value.profiles.filterNot { it.id == id }
                )
            }
            .onFailure {
                _uiState.value = _uiState.value.copy(error = it.message ?: "No se pudo eliminar el perfil")
            }
    }

    /**
     * Limpia el mensaje de error en el estado de la UI
     * Debe ser llamado después de mostrar el error al usuario
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

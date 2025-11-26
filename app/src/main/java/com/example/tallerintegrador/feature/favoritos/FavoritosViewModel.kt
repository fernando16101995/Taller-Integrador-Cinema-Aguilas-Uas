package com.example.tallerintegrador.feature.favoritos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tallerintegrador.data.local.TokenManager
import com.example.tallerintegrador.data.model.pelicula
import com.example.tallerintegrador.data.repository.FavoritosRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * ✅ ViewModel con cache reactivo
 *
 * - Ya no hace requests innecesarias
 * - Usa Flows para actualización automática
 * - Cache optimista para mejor UX
 */
class FavoritosViewModel(
    application: Application,
    private val repository: FavoritosRepository
) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application.applicationContext)

    // ========== ESTADO DE PELÍCULAS FAVORITAS ==========

    private val _favoritos = MutableStateFlow<List<pelicula>>(emptyList())
    val favoritos: StateFlow<List<pelicula>> = _favoritos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // ========== FLOW REACTIVO DE IDS DE FAVORITOS ==========

    /**
     * ✅ Flow que se actualiza automáticamente cuando cambian los favoritos
     * Útil para marcar películas como favoritas en HomeScreen sin requests
     */
    val favoritosIds: StateFlow<Set<Int>> = repository.getFavoritosIdsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    // ========== FUNCIONES PÚBLICAS ==========

    /**
     * ✅ Carga favoritos SOLO cuando sea necesario
     * Ya no se llama automáticamente cada vez
     */
    fun cargarFavoritos() {
        val token = getTokenOrNull() ?: return

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val peliculas = repository.getFavoritos(token)
                _favoritos.value = peliculas
            } catch (e: Exception) {
                _error.value = "Error al cargar favoritos: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * ✅ Toggle con cache optimista
     * La UI se actualiza inmediatamente sin esperar al servidor
     */
    fun toggleFavorito(peliculaId: Int, currentlyFavorite: Boolean) {
        val token = getTokenOrNull() ?: return

        viewModelScope.launch {
            try {
                if (currentlyFavorite) {
                    repository.removeFavorito(token, peliculaId)
                } else {
                    repository.addFavorito(token, peliculaId)
                }

                // Actualiza la lista de películas favoritas
                _favoritos.value = repository.getFavoritos(token)

            } catch (e: Exception) {
                _error.value = "Error al actualizar favorito: ${e.message}"
            }
        }
    }

    /**
     * ✅ Verifica si es favorito desde CACHE (sin red)
     */
    suspend fun esFavorito(peliculaId: Int): Boolean {
        return repository.isFavorito(peliculaId)
    }

    /**
     * ✅ Flow reactivo para observar estado de favorito
     * La UI se actualiza automáticamente cuando cambia
     */
    fun esFavoritoFlow(peliculaId: Int): Flow<Boolean> {
        return repository.isFavoritoFlow(peliculaId)
    }

    /**
     * ✅ Limpia cache al cerrar sesión
     */
    fun clearCache() {
        viewModelScope.launch {
            repository.clearCache()
            _favoritos.value = emptyList()
        }
    }

    // ========== HELPER ==========

    private fun getTokenOrNull(): String? = tokenManager.getToken()
}
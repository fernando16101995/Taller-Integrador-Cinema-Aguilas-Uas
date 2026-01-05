package com.example.tallerintegrador.feature.favoritos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tallerintegrador.data.local.TokenManager
import com.example.tallerintegrador.data.model.pelicula
import com.example.tallerintegrador.data.repository.FavoritosRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritosViewModel @Inject constructor(
    private val repository: FavoritosRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _favoritos = MutableStateFlow<List<pelicula>>(emptyList())
    val favoritos: StateFlow<List<pelicula>> = _favoritos.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Flow reactivo de IDs (ya lo tienes)
    val favoritosIds: StateFlow<Set<Int>> = repository.getFavoritosIdsFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )

    fun cargarFavoritos() {
        viewModelScope.launch {
            try {
                val token = tokenManager.getToken()
                if (token != null) {
                    cargarFavoritosInternamente()
                }
            } catch (e: Exception) {
                android.util.Log.e("FavoritosViewModel", "Error loading favoritos", e)
                _error.value = e.message
                _favoritos.value = emptyList()
            }
        }
    }

    // Método interno privado
    private suspend fun cargarFavoritosInternamente() {
        val token = tokenManager.getToken()
        if (token == null) {
            android.util.Log.w("FavoritosViewModel", "No token available, skipping favoritos load")
            _favoritos.value = emptyList()
            return
        }

        _isLoading.value = true
        _error.value = null

        try {
            val peliculas = repository.getFavoritos(token)
            _favoritos.value = peliculas
            android.util.Log.d("FavoritosViewModel", "Favoritos loaded: ${peliculas.size}")
        } catch (e: Exception) {
            android.util.Log.e("FavoritosViewModel", "Error loading favoritos internamente", e)
            _error.value = null // No mostrar error al usuario
            _favoritos.value = emptyList()
        } finally {
            _isLoading.value = false
        }
    }

    fun toggleFavorito(peliculaId: Int, currentlyFavorite: Boolean) {
        val token = tokenManager.getToken()
        if (token == null) {
            android.util.Log.w("FavoritosViewModel", "No token available for toggleFavorito")
            return
        }

        viewModelScope.launch {
            try {
                if (currentlyFavorite) {
                    repository.removeFavorito(token, peliculaId)
                } else {
                    repository.addFavorito(token, peliculaId)
                }
            } catch (e: Exception) {
                android.util.Log.e("FavoritosViewModel", "Error toggling favorito", e)
                _error.value = null // No mostrar error
            }
        }
    }

    suspend fun esFavorito(peliculaId: Int): Boolean {
        return try {
            repository.isFavorito(peliculaId)
        } catch (e: Exception) {
            android.util.Log.e("FavoritosViewModel", "Error checking isFavorito", e)
            false
        }
    }

    fun esFavoritoFlow(peliculaId: Int): Flow<Boolean> {
        return try {
            repository.isFavoritoFlow(peliculaId)
        } catch (e: Exception) {
            android.util.Log.e("FavoritosViewModel", "Error getting isFavoritoFlow", e)
            flowOf(false)
        }
    }

    fun clearCache() {
        viewModelScope.launch {
            try {
                repository.clearCache()
                _favoritos.value = emptyList()
            } catch (e: Exception) {
                android.util.Log.e("FavoritosViewModel", "Error clearing cache", e)
            }
        }
    }
}
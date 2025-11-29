package com.example.tallerintegrador.feature.favoritos

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tallerintegrador.data.local.TokenManager
import com.example.tallerintegrador.data.model.pelicula
import com.example.tallerintegrador.data.repository.FavoritosRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FavoritosViewModel(
    application: Application,
    private val repository: FavoritosRepository
) : AndroidViewModel(application) {

    private val tokenManager = TokenManager(application.applicationContext)

    private val _favoritos = MutableStateFlow<List<pelicula>>(emptyList())
    val favoritos: StateFlow<List<pelicula>> = _favoritos.asStateFlow()

    private fun getTokenOrNull(): String? = tokenManager.getToken()

    fun cargarFavoritos() {
        viewModelScope.launch {
            val token = getTokenOrNull() ?: return@launch
            try {
                _favoritos.value = repository.getFavoritos(token)
            } catch (_: Exception) {
                // Podrías loguear el error
            }
        }
    }

    fun toggleFavorito(peliculaId: Int, currentlyFavorite: Boolean) {
        viewModelScope.launch {
            val token = getTokenOrNull() ?: return@launch
            try {
                if (currentlyFavorite) {
                    repository.removeFavorito(token, peliculaId)
                } else {
                    repository.addFavorito(token, peliculaId)
                }
                // Actualizamos la lista después del cambio
                _favoritos.value = repository.getFavoritos(token)
            } catch (_: Exception) {
                // Manejo de errores si quieres
            }
        }
    }

    suspend fun esFavorito(peliculaId: Int): Boolean {
        val token = getTokenOrNull() ?: return false
        return repository.isFavorito(token, peliculaId)
    }
}

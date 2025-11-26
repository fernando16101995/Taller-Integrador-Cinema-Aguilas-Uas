package com.example.tallerintegrador.feature.peliculas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tallerintegrador.data.model.pelicula
import com.example.tallerintegrador.data.repository.PeliculaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Estados para la carga de detalles de película
 */
sealed class PeliculaDetailState {
    object Idle : PeliculaDetailState()
    object Loading : PeliculaDetailState()
    data class Success(val pelicula: pelicula) : PeliculaDetailState()
    data class Error(val message: String) : PeliculaDetailState()
}

/**
 * ✅ ViewModel con cache inteligente
 *
 * - Ya no carga películas en init() automáticamente
 * - Usa cache para evitar requests duplicadas
 * - Maneja estado de carga correctamente
 */
class PeliculaViewModel(
    private val repository: PeliculaRepository
) : ViewModel() {

    // --- ESTADO PARA LA LISTA DE PELÍCULAS (HomeScreen) ---

    private val _peliculas = MutableStateFlow<List<pelicula>>(emptyList())
    val peliculas: StateFlow<List<pelicula>> = _peliculas.asStateFlow()

    private val _isLoadingList = MutableStateFlow(false)
    val isLoadingList: StateFlow<Boolean> = _isLoadingList.asStateFlow()

    // --- ESTADO PARA DETALLES DE PELÍCULA ---

    private val _peliculaDetail = MutableStateFlow<PeliculaDetailState>(PeliculaDetailState.Idle)
    val peliculaDetail: StateFlow<PeliculaDetailState> = _peliculaDetail.asStateFlow()

    // ========== LISTA DE PELÍCULAS ==========

    /**
     * ✅ Obtiene películas con cache inteligente
     *
     * @param forceRefresh Si es true, ignora el cache y fuerza red
     */
    fun getPeliculas(forceRefresh: Boolean = false) {
        // Si ya hay películas cargadas y no se fuerza refresh, no hace nada
        if (_peliculas.value.isNotEmpty() && !forceRefresh) {
            return
        }

        viewModelScope.launch {
            _isLoadingList.value = true

            try {
                val peliculasList = repository.getPeliculas(forceRefresh)
                _peliculas.value = peliculasList
            } catch (e: Exception) {
                // Puedes agregar un estado de error si lo necesitas
                _peliculas.value = emptyList()
            } finally {
                _isLoadingList.value = false
            }
        }
    }

    // ========== DETALLES DE PELÍCULA ==========

    /**
     * ✅ Carga detalles con cache inteligente
     *
     * Primero busca en cache, luego en red si es necesario
     */
    fun getPeliculaById(peliculaId: Int, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _peliculaDetail.value = PeliculaDetailState.Loading

            try {
                val pelicula = repository.getPeliculaById(peliculaId, forceRefresh)
                _peliculaDetail.value = PeliculaDetailState.Success(pelicula)
            } catch (e: Exception) {
                _peliculaDetail.value = PeliculaDetailState.Error(
                    e.message ?: "Error al cargar los detalles de la película"
                )
            }
        }
    }

    /**
     * ✅ Método optimizado: busca primero en la lista local
     * Si no está, pide al cache/red
     */
    fun getPeliculaByIdWithFallback(peliculaId: Int) {
        viewModelScope.launch {
            // Intenta buscar en la lista local primero
            val peliculaLocal = _peliculas.value.find { it.id == peliculaId }

            if (peliculaLocal != null) {
                // ✅ Encontrada en memoria
                _peliculaDetail.value = PeliculaDetailState.Success(peliculaLocal)
            } else {
                // ❌ No está en memoria, debe buscar en cache/red
                getPeliculaById(peliculaId)
            }
        }
    }

    /**
     * Limpia el estado de detalles cuando se sale de la pantalla
     */
    fun clearPeliculaDetail() {
        _peliculaDetail.value = PeliculaDetailState.Idle
    }

    // ========== UTILIDADES ==========

    /**
     * ✅ Limpia el cache (útil para logout)
     */
    fun clearCache() {
        viewModelScope.launch {
            repository.clearCache()
            _peliculas.value = emptyList()
        }
    }

    /**
     * ✅ Fuerza refresh de películas
     */
    fun refresh() {
        getPeliculas(forceRefresh = true)
    }
}
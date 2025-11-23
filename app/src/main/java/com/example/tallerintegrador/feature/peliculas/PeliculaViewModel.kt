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

class PeliculaViewModel(private val repository: PeliculaRepository) : ViewModel() {

    // --- ESTADO PARA LA LISTA DE PELÍCULAS (HomeScreen) ---
    private val _peliculas = MutableStateFlow<List<pelicula>>(emptyList())
    val peliculas: StateFlow<List<pelicula>> = _peliculas.asStateFlow()

    // --- ✅ NUEVO: ESTADO ROBUSTO PARA DETALLES DE PELÍCULA ---
    private val _peliculaDetail = MutableStateFlow<PeliculaDetailState>(PeliculaDetailState.Idle)
    val peliculaDetail: StateFlow<PeliculaDetailState> = _peliculaDetail.asStateFlow()

    // Cargar películas automáticamente al iniciar
    init {
        getPeliculas()
    }

    /**
     * Obtiene la lista completa de películas desde el repositorio.
     * Usado por la pantalla principal (HomeScreen).
     */
    fun getPeliculas() {
        viewModelScope.launch {
            try {
                val peliculasList = repository.getPeliculas()
                _peliculas.value = peliculasList
            } catch (e: Exception) {
                // Podrías agregar un estado de error para la lista si lo necesitas
            }
        }
    }

    /**
     * ✅ NUEVO: Carga una película específica por ID directamente desde la API.
     * Esta es la forma ROBUSTA de obtener detalles, ya que no depende de la
     * lista en memoria que podría estar vacía si Android mata la app.
     *
     * @param peliculaId ID de la película a cargar
     */
    fun getPeliculaById(peliculaId: Int) {
        viewModelScope.launch {
            _peliculaDetail.value = PeliculaDetailState.Loading
            try {
                val pelicula = repository.getPeliculaById(peliculaId)
                _peliculaDetail.value = PeliculaDetailState.Success(pelicula)
            } catch (e: Exception) {
                _peliculaDetail.value = PeliculaDetailState.Error(
                    e.message ?: "Error al cargar los detalles de la película"
                )
            }
        }
    }

    /**
     * ✅ MÉTODO DE RESPALDO: Busca primero en la lista local, pero si no encuentra
     * o la lista está vacía, hace una llamada a la API.
     *
     * Este método es útil para optimizar: primero intenta usar datos que ya tiene,
     * pero si no los encuentra, los pide a la API.
     */
    fun getPeliculaByIdWithFallback(peliculaId: Int) {
        viewModelScope.launch {
            // Intenta buscar en la lista local primero
            val peliculaLocal = _peliculas.value.find { it.id == peliculaId }

            if (peliculaLocal != null) {
                // ✅ Encontrada en cache local
                _peliculaDetail.value = PeliculaDetailState.Success(peliculaLocal)
            } else {
                // ❌ No está en cache, debe pedir a la API
                _peliculaDetail.value = PeliculaDetailState.Loading
                try {
                    val pelicula = repository.getPeliculaById(peliculaId)
                    _peliculaDetail.value = PeliculaDetailState.Success(pelicula)
                } catch (e: Exception) {
                    _peliculaDetail.value = PeliculaDetailState.Error(
                        e.message ?: "Error al cargar los detalles de la película"
                    )
                }
            }
        }
    }

    /**
     * Limpia el estado de detalles cuando se sale de la pantalla
     */
    fun clearPeliculaDetail() {
        _peliculaDetail.value = PeliculaDetailState.Idle
    }
}
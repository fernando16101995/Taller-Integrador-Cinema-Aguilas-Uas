package com.example.tallerintegrador.feature.peliculas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tallerintegrador.data.model.pelicula
import com.example.tallerintegrador.data.repository.PeliculaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PeliculaViewModel(private val repository: PeliculaRepository) : ViewModel() {

    // --- ESTADO PARA LA LISTA DE PELÍCULAS (HomeScreen) ---
    private val _peliculas = MutableStateFlow<List<pelicula>>(emptyList())
    val peliculas: StateFlow<List<pelicula>> = _peliculas.asStateFlow() // Usar asStateFlow es una mejor práctica

    // --- NUEVO: ESTADO PARA UNA SOLA PELÍCULA (DetallePeliculaScreen) ---
    private val _peliculaSeleccionada = MutableStateFlow<pelicula?>(null)
    val peliculaSeleccionada: StateFlow<pelicula?> = _peliculaSeleccionada.asStateFlow()

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
                // Manejar el error, por ejemplo, mostrando un mensaje en la UI
                // _errorState.value = "No se pudieron cargar las películas"
            }
        }
    }

    /**
     * NUEVO: Carga los detalles de una película específica por su título.
     * Usado por la pantalla de detalles (DetallePeliculaScreen).
     */
    fun getPeliculaPorTitulo(titulo: String) {
        viewModelScope.launch {
            try {
                // Simula la búsqueda en la lista ya cargada o podrías llamar a un nuevo método del repositorio.
                // Para este ejemplo, buscamos en la lista que ya tenemos.
                // En un caso real, sería mejor: repository.getPeliculaPorTitulo(titulo)
                val peliculaEncontrada = _peliculas.value.find { it.titulo == titulo }
                _peliculaSeleccionada.value = peliculaEncontrada
            } catch (e: Exception) {
                // Manejar el error
                _peliculaSeleccionada.value = null
            }
        }
    }


    /**
     * NUEVO: Cambia el estado de favorito de una película.
     * Esta es la función que resuelve el warning "Assigned value is never read".
     */
    fun toggleFavoriteStatus(pelicula: pelicula) {
        viewModelScope.launch {
            // --- LÓGICA REAL DEBERÍA IR AQUÍ ---
            // 1. Llama a tu repositorio para actualizar el estado en la base de datos o API.
            //    Ejemplo: repository.updateFavoriteStatus(pelicula.id, !pelicula.esFavorita)

            // 2. Por ahora, solo actualizaremos el estado en memoria para que la UI reaccione.
            //    Actualizamos la película seleccionada con el nuevo estado de favorito.
            _peliculaSeleccionada.value = _peliculaSeleccionada.value?.copy(
                // Aquí necesitarías un campo como 'esFavorita' en tu data class
                // esFavorita = !pelicula.esFavorita
            )

            // Solo para depuración, puedes imprimir un mensaje.
            println("Cambiando estado de favorito para: ${pelicula.titulo}")
        }
    }
}

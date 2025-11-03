package com.example.tallerintegrador.feature.peliculas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tallerintegrador.data.model.pelicula
import com.example.tallerintegrador.data.repository.PeliculaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PeliculaViewModel(private val repository: PeliculaRepository) : ViewModel() {

    private val _peliculas = MutableStateFlow<List<pelicula>>(emptyList())
    val peliculas: StateFlow<List<pelicula>> = _peliculas

    fun getPeliculas() {
        viewModelScope.launch {
            try {
                _peliculas.value = repository.getPeliculas()
            } catch (e: Exception) {
                // Manejar el error
            }
        }
    }
}

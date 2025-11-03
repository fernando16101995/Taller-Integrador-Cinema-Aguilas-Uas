package com.example.tallerintegrador.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tallerintegrador.data.repository.PeliculaRepository
import com.example.tallerintegrador.feature.peliculas.PeliculaViewModel

class ViewModelFactory(private val repository: PeliculaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PeliculaViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PeliculaViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

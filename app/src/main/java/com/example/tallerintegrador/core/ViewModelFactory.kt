package com.example.tallerintegrador.core

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tallerintegrador.data.local.cache.CacheManager
import com.example.tallerintegrador.data.network.RetrofitClient
import com.example.tallerintegrador.data.repository.FavoritosRepository
import com.example.tallerintegrador.data.repository.PeliculaRepository
import com.example.tallerintegrador.feature.favoritos.FavoritosViewModel
import com.example.tallerintegrador.feature.peliculas.PeliculaViewModel

/**
 * Factory única para crear todos los ViewModels de la aplicación.
 */
class ViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = when {
            // Caso para PeliculaViewModel
            modelClass.isAssignableFrom(PeliculaViewModel::class.java) -> {
                val apiService = RetrofitClient.instance
                val cacheManager = CacheManager(application)
                val repository = PeliculaRepository(apiService, cacheManager)
                PeliculaViewModel(repository)
            }
            // Caso para FavoritosViewModel
            modelClass.isAssignableFrom(FavoritosViewModel::class.java) -> {
                val apiService = RetrofitClient.instance
                val cacheManager = CacheManager(application)
                val repository = FavoritosRepository(apiService, cacheManager)
                FavoritosViewModel(application, repository)
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }

        @Suppress("UNCHECKED_CAST")
        return viewModel as T
    }
}

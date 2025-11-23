package com.example.tallerintegrador.core

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tallerintegrador.data.network.RetrofitClient
import com.example.tallerintegrador.data.repository.FavoritosRepository
import com.example.tallerintegrador.feature.favoritos.FavoritosViewModel

class FavoritosViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritosViewModel::class.java)) {
            val apiService = RetrofitClient.instance
            val repository = FavoritosRepository(apiService)
            @Suppress("UNCHECKED_CAST")
            return FavoritosViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
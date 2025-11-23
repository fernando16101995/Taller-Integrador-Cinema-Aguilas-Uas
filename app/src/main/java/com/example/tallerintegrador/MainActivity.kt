package com.example.tallerintegrador

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.tallerintegrador.auth.AuthViewModel
import com.example.tallerintegrador.core.ViewModelFactory
import com.example.tallerintegrador.data.network.RetrofitClient
import com.example.tallerintegrador.data.repository.PeliculaRepository
import com.example.tallerintegrador.feature.peliculas.PeliculaViewModel
import com.example.tallerintegrador.ui.theme.TallerIntegradorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TallerIntegradorTheme {
                MainNavigation()
            }
        }
    }
}

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    // Crear una instancia única de AuthViewModel para toda la app
    val authViewModel: AuthViewModel = viewModel()

    // Dependencias para películas
    val apiService = RetrofitClient.instance
    val peliculaRepository = PeliculaRepository(apiService)
    val peliculaViewModelFactory = ViewModelFactory(peliculaRepository)

    NavHost(navController, startDestination = "welcome") {
        composable("welcome") {
            WelcomeScreen(navController)
        }

        composable("login") {
            LoginScreen(navController, authViewModel)
        }

        composable("register") {
            RegisterScreen(navController, authViewModel)
        }

        composable("home") {
            val peliculaViewModel: PeliculaViewModel = viewModel(factory = peliculaViewModelFactory)
            HomeScreen(
                viewModel = peliculaViewModel,
                navController = navController,
                authViewModel = authViewModel
            )
        }

        // ✅ RUTA ACTUALIZADA: Ahora usa ID de película en lugar de título
        composable(
            route = "detalle_pelicula/{peliculaId}",
            arguments = listOf(
                navArgument("peliculaId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val peliculaId = backStackEntry.arguments?.getInt("peliculaId") ?: 0
            val peliculaViewModel: PeliculaViewModel = viewModel(factory = peliculaViewModelFactory)

            DetallePeliculaScreen(
                peliculaId = peliculaId,
                viewModel = peliculaViewModel,
                navController = navController
            )
        }
    }
}
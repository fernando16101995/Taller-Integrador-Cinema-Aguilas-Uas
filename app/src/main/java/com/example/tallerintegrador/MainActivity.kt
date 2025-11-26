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
import com.example.tallerintegrador.feature.peliculas.PeliculaViewModel
import com.example.tallerintegrador.feature.favoritos.FavoritosViewModel
import com.example.tallerintegrador.ui.theme.TallerIntegradorTheme
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

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

/**
 * ✅ NAVEGACIÓN ACTUALIZADA con ruta de películas por género
 */
@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    // ViewModelFactory única para todos los ViewModels
    val viewModelFactory = ViewModelFactory(
        navController.context.applicationContext as android.app.Application
    )

    // ViewModels compartidos
    val authViewModel: AuthViewModel = viewModel()
    val peliculaViewModel: PeliculaViewModel = viewModel(factory = viewModelFactory)
    val favoritosViewModel: FavoritosViewModel = viewModel(factory = viewModelFactory)

    NavHost(navController, startDestination = "welcome") {
        composable("welcome") {
            WelcomeScreen(navController)
        }

        composable("login") {
            LoginScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("register") {
            RegisterScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable("home") {
            HomeScreen(
                viewModel = peliculaViewModel,
                navController = navController,
                authViewModel = authViewModel,
                favoritosViewModel = favoritosViewModel
            )
        }

        // Ruta para detalles de película
        composable(
            route = "detalle_pelicula/{peliculaId}",
            arguments = listOf(
                navArgument("peliculaId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val peliculaId = backStackEntry.arguments?.getInt("peliculaId") ?: 0

            DetallePeliculaScreen(
                peliculaId = peliculaId,
                viewModel = peliculaViewModel,
                navController = navController,
                favoritosViewModel = favoritosViewModel
            )
        }

        // ✅ NUEVA RUTA: Películas por género
        composable(
            route = "peliculas_por_genero/{genero}",
            arguments = listOf(
                navArgument("genero") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val generoEncoded = backStackEntry.arguments?.getString("genero") ?: ""
            // Decodificar el género (por si tiene caracteres especiales como "Ciencia Ficción")
            val genero = URLDecoder.decode(generoEncoded, StandardCharsets.UTF_8.toString())

            PeliculasPorGeneroScreen(
                genero = genero,
                peliculaViewModel = peliculaViewModel,
                favoritosViewModel = favoritosViewModel,
                navController = navController
            )
        }
    }
}
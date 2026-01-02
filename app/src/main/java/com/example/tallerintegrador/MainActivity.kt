package com.example.tallerintegrador

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.tallerintegrador.auth.AuthViewModel
import com.example.tallerintegrador.feature.admin.AdminViewModel
import com.example.tallerintegrador.feature.favoritos.FavoritosViewModel
import com.example.tallerintegrador.feature.peliculas.PeliculaViewModel
import com.example.tallerintegrador.feature.profiles.ProfilesScreen
import com.example.tallerintegrador.ui.theme.TallerIntegradorTheme
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
 * Actividad principal de la aplicación
 * Punto de entrada y contenedor del sistema de navegación
 */
@AndroidEntryPoint
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
 * Sistema de navegación principal de la aplicación
 * Define todas las rutas y pantallas disponibles
 */
@Composable
fun MainNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {

        // Sección de autenticación

        // Pantalla de bienvenida inicial
        composable("welcome") {
            WelcomeScreen(navController)
        }

        // Pantalla de inicio de sesión
        composable("login") {
            val authViewModel: AuthViewModel = hiltViewModel()
            LoginScreen(navController, authViewModel)
        }

        // Pantalla de registro de nuevos usuarios
        composable("register") {
            val authViewModel: AuthViewModel = hiltViewModel()
            RegisterScreen(navController, authViewModel)
        }

        // Pantalla de recuperación de contraseña
        composable("recuperacion") {
            val authViewModel: AuthViewModel = hiltViewModel()
            PantallaRecuperarContrasena(navController, authViewModel)
        }

        // Pantalla de validación de código de recuperación
        composable("validar_codigo/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val authViewModel: AuthViewModel = hiltViewModel()
            PantallaValidarCodigo(email, navController, authViewModel)
        }

        // Pantalla para establecer nueva contraseña
        composable("nueva_contrasena/{email}/{codigo}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val codigo = backStackEntry.arguments?.getString("codigo") ?: ""
            val authViewModel: AuthViewModel = hiltViewModel()
            PantallaNuevaContrasena(email, codigo, navController, authViewModel)
        }

        // Sección principal de la aplicación

        // Pantalla principal con catálogo de películas
        composable("home") {
            val authViewModel: AuthViewModel = hiltViewModel()
            val peliculaViewModel: PeliculaViewModel = hiltViewModel()
            val favoritosViewModel: FavoritosViewModel = hiltViewModel()

            HomeScreen(
                viewModel = peliculaViewModel,
                navController = navController,
                authViewModel = authViewModel,
                favoritosViewModel = favoritosViewModel
            )
        }

        // Sección de películas

        composable(
            route = "detalle_pelicula/{peliculaId}",
            arguments = listOf(
                navArgument("peliculaId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val peliculaId = backStackEntry.arguments?.getInt("peliculaId") ?: 0
            val peliculaViewModel: PeliculaViewModel = hiltViewModel()
            val favoritosViewModel: FavoritosViewModel = hiltViewModel()

            DetallePeliculaScreen(
                peliculaId = peliculaId,
                viewModel = peliculaViewModel,
                navController = navController,
                favoritosViewModel = favoritosViewModel
            )
        }

        composable(
            route = "peliculas_por_genero/{genero}",
            arguments = listOf(
                navArgument("genero") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val generoEncoded = backStackEntry.arguments?.getString("genero") ?: ""
            val genero = URLDecoder.decode(
                generoEncoded,
                StandardCharsets.UTF_8.toString()
            )

            val peliculaViewModel: PeliculaViewModel = hiltViewModel()
            val favoritosViewModel: FavoritosViewModel = hiltViewModel()

            PeliculasPorGeneroScreen(
                genero = genero,
                peliculaViewModel = peliculaViewModel,
                favoritosViewModel = favoritosViewModel,
                navController = navController
            )
        }

        /* -------------------- CONFIG -------------------- */

        composable("configuracion") {
            val peliculaViewModel: PeliculaViewModel = hiltViewModel()
            val favoritosViewModel: FavoritosViewModel = hiltViewModel()

            ConfiguracionScreen(
                navController = navController,
                peliculaViewModel = peliculaViewModel,
                favoritosViewModel = favoritosViewModel
            )
        }

        composable("editar_perfil") {
            val authViewModel: AuthViewModel = hiltViewModel()
            EditarPerfilScreen(navController, authViewModel)
        }

        composable("notificaciones") {
            NotificacionesScreen(navController)
        }

        composable("privacidad") {
            val authViewModel: AuthViewModel = hiltViewModel()
            PrivacidadScreen(navController, authViewModel)
        }

        composable("acerca_de") {
            AcercaDeScreen(navController)
        }

        /* -------------------- ADMIN -------------------- */

        composable("admin/dashboard") {
            val adminViewModel: AdminViewModel = hiltViewModel()
            AdminDashboardScreen(navController, adminViewModel)
        }

        composable("admin/usuarios") {
            val adminViewModel: AdminViewModel = hiltViewModel()
            AdminUsuariosScreen(navController, adminViewModel)
        }

        composable("admin/peliculas") {
            val adminViewModel: AdminViewModel = hiltViewModel()
            AdminPeliculasScreen(navController, adminViewModel)
        }

        // Sección de administración

        // Pantalla de logs de actividad del administrador
        composable("admin/logs") {
            val adminViewModel: AdminViewModel = hiltViewModel()
            AdminLogsScreen(navController, adminViewModel)
        }

        // Formulario para crear nueva película (administrador)
        composable("admin/peliculas/nueva") {
            val adminViewModel: AdminViewModel = hiltViewModel()

            LaunchedEffect(Unit) {
                adminViewModel.cargarPeliculas()
            }

            AdminPeliculaFormScreen(
                navController = navController,
                peliculaId = null,
                adminViewModel = adminViewModel
            )
        }

        // Formulario para editar película existente (administrador)
        composable(
            route = "admin/peliculas/editar/{peliculaId}",
            arguments = listOf(
                navArgument("peliculaId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val peliculaId =
                backStackEntry.arguments?.getInt("peliculaId") ?: return@composable

            val adminViewModel: AdminViewModel = hiltViewModel()

            LaunchedEffect(Unit) {
                adminViewModel.cargarPeliculas()
            }

            AdminPeliculaFormScreen(
                navController = navController,
                peliculaId = peliculaId,
                adminViewModel = adminViewModel
            )
        }

        // Pantalla de selección de perfiles
        // Permite elegir entre perfiles de usuario (adulto/niño)
        composable("profiles") {
            ProfilesScreen(
                onProfileSelected = {
                    // Después de seleccionar perfil, ir a verificar suscripción
                    navController.navigate("subscription") {
                        popUpTo("profiles") { inclusive = true }
                    }
                }
            )
        }

        // Pantalla de suscripción
        // Verifica si el usuario tiene suscripción activa
        // Si no tiene, muestra opciones de pago con Stripe
        composable("subscription") {
            com.example.tallerintegrador.feature.subscription.SubscriptionScreen(
                navController = navController
            )
        }

    }
}

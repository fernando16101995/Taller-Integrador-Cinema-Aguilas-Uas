package com.example.tallerintegrador

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.tallerintegrador.auth.AuthViewModel
import com.example.tallerintegrador.feature.admin.AdminViewModel
import com.example.tallerintegrador.feature.favoritos.FavoritosViewModel
import com.example.tallerintegrador.feature.peliculas.PeliculaViewModel
import com.example.tallerintegrador.feature.profiles.ProfileFormScreen
import com.example.tallerintegrador.feature.profiles.ProfilesScreen
import com.example.tallerintegrador.feature.profiles.ProfilesViewModel
import com.example.tallerintegrador.feature.subscription.SubscriptionCheckoutScreen
import com.example.tallerintegrador.feature.subscription.SubscriptionScreen
import com.example.tallerintegrador.feature.subscription.SubscriptionViewModel
import com.example.tallerintegrador.ui.theme.TallerIntegradorTheme
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

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
        // Catálogo público con solo portadas
        composable("catalogo_publico") {
            val peliculaViewModel: PeliculaViewModel = hiltViewModel()
            CatalogoPublicoScreen(navController, peliculaViewModel)
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
            val peliculaViewModel: PeliculaViewModel = hiltViewModel()
            PrivacidadScreen(navController, authViewModel, peliculaViewModel)
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
            val profilesViewModel: ProfilesViewModel = hiltViewModel()
            ProfilesScreen(
                vm = profilesViewModel,
                onProfileSelected = {
                    navController.navigate("subscription") {
                        popUpTo("profiles") { inclusive = true }
                    }
                },
                onCreateProfile = {
                    navController.navigate("profiles/create")
                },
                onEditProfile = { profile ->
                    val encodedId = Uri.encode(profile.id)
                    navController.navigate("profiles/edit/$encodedId")
                }
            )
        }
        composable("profiles/create") {
            val profilesViewModel: ProfilesViewModel = hiltViewModel()
            LaunchedEffect(Unit) {
                profilesViewModel.saved.collect {
                    navController.popBackStack()
                }
            }
            ProfileFormScreen(
                onSave = { name, avatarUrl, isKids ->
                    profilesViewModel.create(name, avatarUrl.ifBlank { null }, isKids)
                },
                onCancel = { navController.popBackStack() }
            )
        }
        composable(
            route = "profiles/edit/{profileId}",
            arguments = listOf(navArgument("profileId") { type = NavType.StringType })
        ) { backStackEntry ->
            val profileId = backStackEntry.arguments?.getString("profileId") ?: return@composable
            val profilesViewModel: ProfilesViewModel = hiltViewModel()
            val uiState = profilesViewModel.uiState.collectAsState()
            val profile = uiState.value.profiles.find { it.id == profileId }
            LaunchedEffect(Unit) {
                profilesViewModel.saved.collect {
                    navController.popBackStack()
                }
            }
            LaunchedEffect(profileId) {
                if (profile == null) {
                    profilesViewModel.load()
                }
            }
            if (profile == null) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                ProfileFormScreen(
                    initialName = profile.nombre,
                    initialAvatarUrl = profile.avatarUrl ?: "",
                    initialIsKids = profile.esNino,
                    onSave = { name, avatarUrl, isKids ->
                        profilesViewModel.update(profile.id, name, avatarUrl.ifBlank { null }, isKids)
                    },
                    onCancel = { navController.popBackStack() },
                    onDelete = {
                        profilesViewModel.delete(profile.id)
                        navController.popBackStack()
                    }
                )
            }
        }

        // Pantalla de suscripción
        // Verifica si el usuario tiene suscripción activa
        // Si no tiene, muestra opciones de pago con Stripe
        composable("subscription") {
            val subscriptionViewModel: SubscriptionViewModel = hiltViewModel()
            SubscriptionScreen(
                navController = navController,
                subscriptionViewModel = subscriptionViewModel
            )
        }
        composable(
            route = "stripeResult/{status}?sessionId={sessionId}",
            arguments = listOf(
                navArgument("sessionId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = "cinemaaguilas://stripe/{status}?session_id={sessionId}" }
            )
        ) { backStackEntry ->
            val status = backStackEntry.arguments?.getString("status") ?: ""
            val sessionId = backStackEntry.arguments?.getString("sessionId")
            val subscriptionViewModel: SubscriptionViewModel = hiltViewModel()
            LaunchedEffect(status) {
                subscriptionViewModel.handleCheckoutCallback(status, sessionId)
                navController.navigate("subscription") {
                    popUpTo("subscription") { inclusive = true }
                }
            }
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        composable(
            route = "subscription/checkout/{encodedUrl}",
            arguments = listOf(navArgument("encodedUrl") { type = NavType.StringType })
        ) { backStackEntry ->
            val encodedUrl = backStackEntry.arguments?.getString("encodedUrl") ?: return@composable
            SubscriptionCheckoutScreen(
                navController = navController,
                encodedUrl = encodedUrl
            )
        }
    }
}

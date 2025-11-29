package com.example.tallerintegrador

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tallerintegrador.data.model.pelicula
import com.example.tallerintegrador.feature.peliculas.PeliculaViewModel
import com.example.tallerintegrador.ui.theme.DarkBlue
import com.example.tallerintegrador.ui.theme.TallerIntegradorTheme
import com.example.tallerintegrador.ui.theme.Yellow
import com.example.tallerintegrador.feature.favoritos.FavoritosViewModel
import com.example.tallerintegrador.core.FavoritosViewModelFactory
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalContext
import android.app.Application
import com.example.tallerintegrador.auth.AuthViewModel


@Composable
fun HomeScreen(
    viewModel: PeliculaViewModel,
    navController: NavController? = null,
    authViewModel: AuthViewModel = viewModel()
) {
    val peliculas by viewModel.peliculas.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        // Solo recarga si por alguna razón viene vacío
        if (viewModel.peliculas.value.isEmpty()) {
            viewModel.getPeliculas()
        }
    }

    val peliculasPorGenero = mutableMapOf<String, MutableList<pelicula>>()
    peliculas.forEach { pelicula ->
        pelicula.genre.split(',').forEach { genero ->
            val trimmedGenero = genero.trim()
            peliculasPorGenero.getOrPut(trimmedGenero) { mutableListOf() }.add(pelicula)
        }
    }

    HomeScreenContent(
        peliculasPorGenero = peliculasPorGenero,
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        viewModel = viewModel,
        navController = navController,
        authViewModel = authViewModel
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    peliculasPorGenero: Map<String, List<pelicula>>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    viewModel: PeliculaViewModel? = null,
    navController: NavController? = null,
    authViewModel: AuthViewModel? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CinemasAguilasUas", color = Yellow) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkBlue, // Color de fondo
                    titleContentColor = Yellow, // Color del título
                    actionIconContentColor = Color.White // Color de los iconos de acción
                ),
                actions = {
                    TextButton(onClick = {
                        // Ir a la pestaña de perfil
                        onTabSelected(4)
                    }) {
                        Text("Mi perfil", color = Yellow)
                    }
                    TextButton(onClick = {
                        // ⭐ Cerrar sesión desde el TopBar
                        authViewModel?.logout()
                        navController?.navigate("welcome") {
                            popUpTo(0) { inclusive = true }
                        }
                    }) {
                        Text("Cerrar sesión", color = Color.White)
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected
            )
        },
        containerColor = DarkBlue // <--- Parámetro correcto para el fondo
    ) { padding ->
        when (selectedTab) {
            0 -> {
                // Pantalla de Inicio
                LazyColumn(modifier = Modifier.padding(padding)) {
                    peliculasPorGenero.forEach { (genero, peliculasDelGenero) ->
                        item {
                            Text(
                                text = genero,
                                color = Yellow,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp)
                            )
                        }
                        item {
                            LazyRow(
                                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(peliculasDelGenero) { pelicula ->
                                    MovieCard(
                                        pelicula = pelicula,
                                        onClick = {

                                            navController?.navigate("detalle_pelicula/${pelicula.id}")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            1 -> {
                // Pantalla de Categorías
                Box(modifier = Modifier.padding(padding)) {
                    CategoriasScreen()
                }
            }
            2 -> {
                // Pantalla de Búsqueda
                Box(modifier = Modifier.padding(padding)) {
                    viewModel?.let { BusquedaScreen(viewModel = it,
                        navController = navController) }
                }
            }
            3 -> {
                val context = LocalContext.current
                val favoritosViewModel: FavoritosViewModel = viewModel(
                    factory = FavoritosViewModelFactory(context.applicationContext as Application)
                )

                // 🔥 Cargar favoritos SIEMPRE que se abra la pestaña
                LaunchedEffect(Unit) {
                    favoritosViewModel.cargarFavoritos()
                }

                Box(modifier = Modifier.padding(paddingValues = padding)) {
                    FavoritosScreen(
                        peliculaViewModel = viewModel,
                        navController = navController,
                        favoritosViewModel = favoritosViewModel   // <-- se lo pasamos
                    )
                }
            }


            4 -> {
                // Pantalla de Perfil
                Box(modifier = Modifier.padding(padding)) {
                    authViewModel?.let {
                        PerfilScreen(navController, it)
                    }
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = DarkBlue, // Color de fondo de la barra
        contentColor = Yellow, // Color por defecto para el contenido
        tonalElevation = 8.dp // Equivalente a 'elevation'
    ) {
        // Ítem de Inicio
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
            label = { Text("Inicio", fontSize = 11.sp) },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Yellow,
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                selectedTextColor = Yellow,
                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                indicatorColor = DarkBlue.copy(alpha=0.2f) // Color del "círculo" indicador
            )
        )
        // Ítem de Categorías
        NavigationBarItem(
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Categorías") },
            label = { Text("Categorías", fontSize = 11.sp) },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Yellow,
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                selectedTextColor = Yellow,
                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                indicatorColor = DarkBlue.copy(alpha=0.2f)
            )
        )
        // Ítem de Búsqueda
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Search, contentDescription = "Búsqueda") },
            label = { Text("Búsqueda", fontSize = 11.sp) },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Yellow,
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                selectedTextColor = Yellow,
                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                indicatorColor = DarkBlue.copy(alpha=0.2f)
            )
        )
        // Ítem de Favoritos
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favoritos") },
            label = { Text("Favoritos", fontSize = 11.sp) },
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Yellow,
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                selectedTextColor = Yellow,
                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                indicatorColor = DarkBlue.copy(alpha=0.2f)
            )
        )
        // Ítem de Perfil
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil") },
            label = { Text("Perfil", fontSize = 11.sp) },
            selected = selectedTab == 4,
            onClick = { onTabSelected(4) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Yellow,
                unselectedIconColor = Color.White.copy(alpha = 0.6f),
                selectedTextColor = Yellow,
                unselectedTextColor = Color.White.copy(alpha = 0.6f),
                indicatorColor = DarkBlue.copy(alpha=0.2f)
            )
        )
    }
}


@Composable
fun MovieCard(pelicula: pelicula, onClick: () -> Unit = {}) {
    Column(
        modifier = Modifier
            .width(150.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage( // <-- Componente moderno de Coil
            model = pelicula.posterUrl, // Se usa el parámetro 'model'
            contentDescription = pelicula.title,
            modifier = Modifier
                .width(150.dp)
                .height(225.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = pelicula.title,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val dummyPeliculas = listOf(
        pelicula(id = 1,title = "The Dark Knight", description = "...", posterUrl = "url", videoUrl = "url", durationMinutes = 152, genre = "Acción, Crimen"),
        pelicula(id = 2,title = "Mad Max: Fury Road", description = "...", posterUrl = "url", videoUrl = "url", durationMinutes = 120, genre = "Acción"),
        pelicula(id = 3, title = "Shrek", description = "...", posterUrl = "url", videoUrl = "url", durationMinutes = 90, genre = "Animación"),
        pelicula(id = 4,title = "Spider-Man: Into the Spider-Verse", description = "...", posterUrl = "url", videoUrl = "url", durationMinutes = 117, genre = "Animación")
    )
    val peliculasPorGenero = mutableMapOf<String, MutableList<pelicula>>()
    dummyPeliculas.forEach { pelicula ->
        pelicula.genre.split(',').forEach { genero ->
            val trimmedGenero = genero.trim()
            peliculasPorGenero.getOrPut(trimmedGenero) { mutableListOf() }.add(pelicula)
        }
    }

    TallerIntegradorTheme {
        HomeScreenContent(
            peliculasPorGenero = peliculasPorGenero,
            selectedTab = 0,
            onTabSelected = {},
            viewModel = null,
            navController = null
        )
    }
}

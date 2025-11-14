package com.example.tallerintegrador

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.tallerintegrador.data.model.pelicula
import com.example.tallerintegrador.feature.peliculas.PeliculaViewModel
import com.example.tallerintegrador.ui.theme.DarkBlue
import com.example.tallerintegrador.ui.theme.TallerIntegradorTheme
import com.example.tallerintegrador.ui.theme.Yellow

@Composable
fun HomeScreen(viewModel: PeliculaViewModel, navController: androidx.navigation.NavController? = null) {
    val peliculas by viewModel.peliculas.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        viewModel.getPeliculas()
    }

    val peliculasPorGenero = mutableMapOf<String, MutableList<pelicula>>()
    peliculas.forEach { pelicula ->
        pelicula.genero.split(',').forEach { genero ->
            val trimmedGenero = genero.trim()
            peliculasPorGenero.getOrPut(trimmedGenero) { mutableListOf() }.add(pelicula)
        }
    }

    HomeScreenContent(
        peliculasPorGenero = peliculasPorGenero,
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        viewModel = viewModel,
        navController = navController
    )
}

@Composable
fun HomeScreenContent(
    peliculasPorGenero: Map<String, List<pelicula>>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    viewModel: PeliculaViewModel? = null,
    navController: androidx.navigation.NavController? = null
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("CinemasAguilasUas", color = Yellow) },
                backgroundColor = DarkBlue,
                actions = {
                    TextButton(onClick = { /* TODO: Mi perfil */ }) {
                        Text("Mi perfil", color = Yellow)
                    }
                    TextButton(onClick = { /* TODO: Cerrar sesión */ }) {
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
        backgroundColor = DarkBlue
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
                                    MovieCard(pelicula = pelicula)
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
                    viewModel?.let { BusquedaScreen(it) }
                }
            }
            3 -> {
                // Pantalla de Favoritos
                Box(modifier = Modifier.padding(padding)) {
                    viewModel?.let { FavoritosScreen(it) }
                }
            }
            4 -> {
                // Pantalla de Perfil
                Box(modifier = Modifier.padding(padding)) {
                    PerfilScreen(navController)
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
    BottomNavigation(
        backgroundColor = DarkBlue,
        contentColor = Yellow,
        elevation = 8.dp
    ) {
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Inicio") },
            label = { Text("Inicio", fontSize = 11.sp) },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            selectedContentColor = Yellow,
            unselectedContentColor = Color.White.copy(alpha = 0.6f)
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.List, contentDescription = "Categorías") },
            label = { Text("Categorías", fontSize = 11.sp) },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            selectedContentColor = Yellow,
            unselectedContentColor = Color.White.copy(alpha = 0.6f)
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Search, contentDescription = "Búsqueda") },
            label = { Text("Búsqueda", fontSize = 11.sp) },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            selectedContentColor = Yellow,
            unselectedContentColor = Color.White.copy(alpha = 0.6f)
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favoritos") },
            label = { Text("Favoritos", fontSize = 11.sp) },
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            selectedContentColor = Yellow,
            unselectedContentColor = Color.White.copy(alpha = 0.6f)
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Perfil") },
            label = { Text("Perfil", fontSize = 11.sp) },
            selected = selectedTab == 4,
            onClick = { onTabSelected(4) },
            selectedContentColor = Yellow,
            unselectedContentColor = Color.White.copy(alpha = 0.6f)
        )
    }
}

@Composable
fun MovieCard(pelicula: pelicula) {
    Column(
        modifier = Modifier.width(150.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = rememberAsyncImagePainter(pelicula.posterUrl),
            contentDescription = pelicula.titulo,
            modifier = Modifier
                .width(150.dp)
                .height(225.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = pelicula.titulo,
            color = Color.White,
            fontSize = 14.sp,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    val dummyPeliculas = listOf(
        pelicula("The Dark Knight", "...", "Acción, Crimen", 152, "url", "url"),
        pelicula("Mad Max: Fury Road", "...", "Acción", 120, "url", "url"),
        pelicula("Shrek", "...", "Animación", 90, "url", "url"),
        pelicula("Spider-Man: Into the Spider-Verse", "...", "Animación", 117, "url", "url")
    )
    val peliculasPorGenero = mutableMapOf<String, MutableList<pelicula>>()
    dummyPeliculas.forEach { pelicula ->
        pelicula.genero.split(',').forEach { genero ->
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
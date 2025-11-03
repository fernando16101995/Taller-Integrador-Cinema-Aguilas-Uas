package com.example.tallerintegrador

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
fun HomeScreen(viewModel: PeliculaViewModel) {
    val peliculas by viewModel.peliculas.collectAsState()

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

    HomeScreenContent(peliculasPorGenero = peliculasPorGenero)
}

@Composable
fun HomeScreenContent(peliculasPorGenero: Map<String, List<pelicula>>) {
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
        backgroundColor = DarkBlue
    ) { padding ->
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
        HomeScreenContent(peliculasPorGenero = peliculasPorGenero)
    }
}

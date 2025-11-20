package com.example.tallerintegrador


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape


import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults // Necesario para .cardColors y .cardElevation
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import coil.compose.AsyncImage // El import para la nueva forma de cargar imágenes


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.tallerintegrador.data.model.pelicula
import com.example.tallerintegrador.feature.peliculas.PeliculaViewModel
import com.example.tallerintegrador.ui.theme.DarkBlue
import com.example.tallerintegrador.ui.theme.Yellow

@Composable
fun FavoritosScreen(viewModel: PeliculaViewModel) {
    // TODO: En una implementación real, deberías tener una lista de favoritos guardada
    // Por ahora, mostramos las primeras 3 películas como ejemplo
    val peliculas by viewModel.peliculas.collectAsState()
    val peliculasFavoritas = remember { mutableStateListOf<pelicula>() }

    LaunchedEffect(peliculas) {
        if (peliculasFavoritas.isEmpty() && peliculas.isNotEmpty()) {
            peliculasFavoritas.addAll(peliculas.take(3))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mis Favoritos",
                color = Yellow,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Favoritos",
                tint = Yellow,
                modifier = Modifier.size(32.dp)
            )
        }

        if (peliculasFavoritas.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Sin favoritos",
                        tint = Yellow.copy(alpha = 0.5f),
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No tienes películas favoritas",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "¡Agrega tus películas favoritas!",
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 14.sp
                    )
                }
            }
        } else {
            Text(
                text = "${peliculasFavoritas.size} película(s) favorita(s)",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(peliculasFavoritas) { pelicula ->
                    FavoritoItem(
                        pelicula = pelicula,
                        onRemove = { peliculasFavoritas.remove(pelicula) }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoritoItem(pelicula: pelicula, onRemove: () -> Unit) {
    // --- CÓDIGO NUEVO Y CORRECTO ---
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Ver detalles de la película */ },
        shape = RoundedCornerShape(12.dp),
        // Inicio de la corrección de errores
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
        // Fin de la corrección de errores
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Corrección de la carga de imagen (práctica recomendada)
            AsyncImage(
                model = pelicula.posterUrl,
                contentDescription = pelicula.titulo,
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            // El resto del contenido de la fila no necesita cambios
            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = pelicula.titulo,
                    color = Yellow,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = pelicula.genero,
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${pelicula.duracion} min",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = pelicula.sinopsis,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onRemove,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Eliminar de favoritos",
                    tint = Color.Red.copy(alpha = 0.8f)
                )
            }
        }
    }
}
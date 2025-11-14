package com.example.tallerintegrador

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.tallerintegrador.data.model.pelicula
import com.example.tallerintegrador.feature.peliculas.PeliculaViewModel
import com.example.tallerintegrador.ui.theme.DarkBlue
import com.example.tallerintegrador.ui.theme.Yellow

@Composable
fun BusquedaScreen(viewModel: PeliculaViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    val peliculas by viewModel.peliculas.collectAsState()

    val peliculasFiltradas = if (searchQuery.isEmpty()) {
        emptyList()
    } else {
        peliculas.filter {
            it.titulo.contains(searchQuery, ignoreCase = true) ||
                    it.genero.contains(searchQuery, ignoreCase = true) ||
                    it.sinopsis.contains(searchQuery, ignoreCase = true)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue)
            .padding(16.dp)
    ) {
        Text(
            text = "Buscar Películas",
            color = Yellow,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Barra de búsqueda
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Buscar por título, género...", color = Color.White.copy(alpha = 0.6f)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = "Buscar",
                    tint = Yellow
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Limpiar",
                            tint = Yellow
                        )
                    }
                }
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = Color.White,
                cursorColor = Yellow,
                focusedBorderColor = Yellow,
                unfocusedBorderColor = Yellow.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Resultados de búsqueda
        if (searchQuery.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Buscar",
                        tint = Yellow.copy(alpha = 0.5f),
                        modifier = Modifier.size(80.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Busca tus películas favoritas",
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 18.sp
                    )
                }
            }
        } else if (peliculasFiltradas.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No se encontraron resultados",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 18.sp
                )
            }
        } else {
            Text(
                text = "${peliculasFiltradas.size} resultado(s)",
                color = Yellow,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(peliculasFiltradas) { pelicula ->
                    SearchResultItem(pelicula)
                }
            }
        }
    }
}

@Composable
fun SearchResultItem(pelicula: pelicula) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO: Ver detalles de la película */ },
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color.White.copy(alpha = 0.1f),
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(pelicula.posterUrl),
                contentDescription = pelicula.titulo,
                modifier = Modifier
                    .width(80.dp)
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

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
        }
    }
}
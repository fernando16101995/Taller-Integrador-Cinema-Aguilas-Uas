package com.example.tallerintegrador

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Place

import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tallerintegrador.ui.theme.DarkBlue
import com.example.tallerintegrador.ui.theme.Yellow

data class Categoria(
    val nombre: String,
    val icon: ImageVector,
    val color: Color
)

@Composable
fun CategoriasScreen() {
    val categorias = listOf(
        Categoria("Acción", Icons.Filled.Star, Color(0xFFFF5722)),
        Categoria("Comedia", Icons.Filled.Face, Color(0xFFFFC107)),
        Categoria("Drama", Icons.Filled.Favorite, Color(0xFFE91E63)),
        Categoria("Terror", Icons.Filled.Warning, Color(0xFF9C27B0)),
        Categoria("Ciencia Ficción", Icons.AutoMirrored.Filled.Send, Color(0xFF2196F3)),
        Categoria("Romance", Icons.Filled.FavoriteBorder, Color(0xFFF48FB1)),
        Categoria("Thriller", Icons.Filled.Lock, Color(0xFF607D8B)),
        Categoria("Animación", Icons.Filled.Star, Color(0xFF4CAF50)),
        Categoria("Aventura", Icons.Filled.Place, Color(0xFF00BCD4)),
        Categoria("Fantasía", Icons.Filled.Info, Color(0xFF673AB7)),
        Categoria("Documental", Icons.Filled.DateRange, Color(0xFF795548)),
        Categoria("Crimen", Icons.Filled.Build, Color(0xFF424242))
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue)
            .padding(16.dp)
    ) {
        Text(
            text = "Explorar Categorías",
            color = Yellow,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categorias) { categoria ->
                CategoriaCard(categoria)
            }
        }
    }
}

@Composable
fun CategoriaCard(categoria: Categoria) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clickable { /* TODO: Navegar a películas por categoría */ },
        shape = RoundedCornerShape(12.dp),
        backgroundColor = categoria.color.copy(alpha = 0.2f),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = categoria.icon,
                contentDescription = categoria.nombre,
                tint = categoria.color,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = categoria.nombre,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

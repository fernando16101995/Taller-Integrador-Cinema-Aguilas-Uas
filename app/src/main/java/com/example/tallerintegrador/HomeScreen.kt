package com.example.tallerintegrador

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.tallerintegrador.ui.theme.TallerIntegradorTheme

@Composable
fun HomeScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "¡Bienvenido!")
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    TallerIntegradorTheme {
        HomeScreen()
    }
}
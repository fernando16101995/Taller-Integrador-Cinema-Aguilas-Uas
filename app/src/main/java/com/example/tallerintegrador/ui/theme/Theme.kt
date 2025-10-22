package com.example.tallerintegrador.ui.theme

import androidx.compose.material.*
import androidx.compose.runtime.Composable

private val DarkColorPalette = darkColors(
    primary = Yellow,
    background = DarkBlue,
    surface = DarkBlue,
    onPrimary = DarkBlue,
    onBackground = White,
    onSurface = White,
)

@Composable
fun TallerIntegradorTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = DarkColorPalette,
        typography = Typography(),
        shapes = Shapes(),
        content = content
    )
}
package com.example.tallerintegrador.ui.theme

import androidx.compose.material3.Typography // <-- ¡IMPORTE CORRECTO!
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Conjunto de estilos de tipografía de Material 3 para empezar
val Typography = Typography(
    // El estilo 'body1' de M2 ahora se llama 'bodyLarge' en M3
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp, // Es buena práctica definir la altura de línea
        letterSpacing = 0.5.sp
    )
    /* Otros estilos de texto por defecto para sobrescribir
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

package com.example.tallerintegrador.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Define tu paleta para el tema oscuro usando las nuevas propiedades de Material 3
private val DarkColorScheme = darkColorScheme(
    primary = Yellow,
    background = DarkBlue,
    surface = DarkBlue,
    onPrimary = DarkBlue,
    onBackground = White,
    onSurface = White,
    // Puedes añadir más colores de M3 si quieres más personalización
    // secondary = PurpleGrey80,
    // tertiary = Pink80
)

// Aunque no la uses, es buena práctica tener una paleta de tema claro
private val LightColorScheme = lightColorScheme(
    primary = Yellow,
    background = White,
    surface = White,
    onPrimary = DarkBlue,
    onBackground = DarkBlue,
    onSurface = DarkBlue
)

@Composable
fun TallerIntegradorTheme(
    // Tu app es siempre oscura, así que podemos forzarlo.
    darkTheme: Boolean = true,
    // El color dinámico (Material You) no es necesario para tu diseño, así que lo desactivamos.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Para tu caso, siempre será DarkColorScheme
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Código estándar para controlar la barra de estado
    val view = LocalView.current
    if (!view.isInEditMode) {
        // --- CÓDIGO NUEVO Y CORRECTO ---
        SideEffect {
            val window = (view.context as Activity).window
            // Le decimos a la ventana que tu app dibujará su propio fondo detrás de las barras del sistema.
            // Esto es clave para el modo "edge-to-edge" moderno.
            WindowCompat.setDecorFitsSystemWindows(window, false)

            // El Scaffold se encargará de poner el color correcto en la barra de estado.
            // Ya no necesitas window.statusBarColor.

            // Esta línea sigue siendo importante para controlar si los iconos de la barra
            // (reloj, batería, etc.) son oscuros o claros.
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }

    }

    // Usa el MaterialTheme de Material 3
    MaterialTheme(
        colorScheme = colorScheme, // El parámetro ahora es 'colorScheme'
        typography = Typography, // 'Typography()' se puede simplificar a 'Typography'
        content = content
    )
}

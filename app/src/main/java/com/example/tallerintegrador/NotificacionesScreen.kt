package com.example.tallerintegrador

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.edit
import androidx.navigation.NavController
import kotlinx.coroutines.launch

/*
 * Archivo: NotificacionesScreen.kt
 *
 * Pantalla que permite gestionar las notificaciones de la aplicacion.
 *
 * Control General:
 * - Activar/Desactivar Notificaciones: Switch principal que controla
 *   todas las notificaciones de la app
 *
 * Categorias de Contenido:
 * - Nuevos Estrenos: Notifica cuando se agregan peliculas o series nuevas
 * - Recomendaciones: Notifica sobre contenido personalizado para el usuario
 * - Proximos Episodios: Notifica cuando hay nuevos capitulos de series
 * - Actualizaciones de la App: Notifica sobre nuevas funciones y mejoras
 *
 * Todas las preferencias se guardan en SharedPreferences con la clave
 * "notif_prefs" y persisten entre sesiones de la aplicacion.
 *
 * Cada opcion puede activarse/desactivarse de forma independiente,
 * pero requiere que el control general este activado para funcionar.
 */

/**
 * Pantalla de notificaciones con soporte de tema
 * Muestra notificaciones del sistema y permite gestionarlas
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificacionesScreen(navController: NavController?) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("notif_prefs", Context.MODE_PRIVATE) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Estados de notificaciones
    var notificacionesGlobales by remember {
        mutableStateOf(prefs.getBoolean("global", true))
    }
    var nuevosEstrenos by remember {
        mutableStateOf(prefs.getBoolean("nuevos_estrenos", true))
    }
    var recomendaciones by remember {
        mutableStateOf(prefs.getBoolean("recomendaciones", true))
    }
    var proximosEpisodios by remember {
        mutableStateOf(prefs.getBoolean("proximos_episodios", true))
    }
    var actualizaciones by remember {
        mutableStateOf(prefs.getBoolean("actualizaciones", true))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Notificaciones",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController?.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ACTIVACIÓN GLOBAL
            item {
                SectionHeaderNotif("Control General")
            }

            item {
                NotificationSwitchItem(
                    icon = Icons.Filled.Notifications,
                    title = "Activar Notificaciones",
                    subtitle = "Habilitar todas las notificaciones",
                    checked = notificacionesGlobales,
                    onCheckedChange = {
                        notificacionesGlobales = it
                        prefs.edit { putBoolean("global", it) }
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (it) "Notificaciones activadas"
                                else "Notificaciones desactivadas"
                            )
                        }
                    },
                    prominent = true
                )
            }

            // CATEGORÍAS
            item {
                SectionHeaderNotif("Categorías de Contenido")
            }

            item {
                NotificationSwitchItem(
                    icon = Icons.Filled.NewReleases,
                    title = "Nuevos Estrenos",
                    subtitle = "Películas y series recién agregadas",
                    checked = nuevosEstrenos,
                    onCheckedChange = {
                        nuevosEstrenos = it
                        prefs.edit { putBoolean("nuevos_estrenos", it) }
                    },
                    enabled = notificacionesGlobales
                )
            }

            item {
                NotificationSwitchItem(
                    icon = Icons.Filled.Recommend,
                    title = "Recomendaciones",
                    subtitle = "Contenido personalizado para ti",
                    checked = recomendaciones,
                    onCheckedChange = {
                        recomendaciones = it
                        prefs.edit { putBoolean("recomendaciones", it) }
                    },
                    enabled = notificacionesGlobales
                )
            }

            item {
                NotificationSwitchItem(
                    icon = Icons.Filled.PlayCircle,
                    title = "Próximos Episodios",
                    subtitle = "Nuevos capítulos de series que sigues",
                    checked = proximosEpisodios,
                    onCheckedChange = {
                        proximosEpisodios = it
                        prefs.edit { putBoolean("proximos_episodios", it) }
                    },
                    enabled = notificacionesGlobales
                )
            }

            item {
                NotificationSwitchItem(
                    icon = Icons.Filled.Update,
                    title = "Actualizaciones de la App",
                    subtitle = "Nuevas funciones y mejoras",
                    checked = actualizaciones,
                    onCheckedChange = {
                        actualizaciones = it
                        prefs.edit { putBoolean("actualizaciones", it) }
                    },
                    enabled = notificacionesGlobales
                )
            }

            // INFORMACIÓN
            item {
                SectionHeaderNotif("Información")
            }

            item {
                InfoCard(
                    icon = Icons.Filled.Info,
                    title = "Acerca de las notificaciones",
                    description = "Las notificaciones te mantienen al día con nuevo contenido, " +
                            "recomendaciones personalizadas y actualizaciones importantes. " +
                            "Puedes personalizar qué tipo de notificaciones deseas recibir."
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SectionHeaderNotif(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
fun NotificationSwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    prominent: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (prominent)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (enabled)
                    MaterialTheme.colorScheme.primary
                else
                    Color.Gray,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (enabled)
                        MaterialTheme.colorScheme.onSurface
                    else
                        Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = if (prominent) FontWeight.Bold else FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = if (enabled)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        Color.Gray.copy(alpha = 0.4f),
                    fontSize = 13.sp
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f),
                    disabledCheckedThumbColor = Color.Gray,
                    disabledUncheckedThumbColor = Color.DarkGray
                )
            )
        }
    }
}

@Composable
fun InfoCard(
    icon: ImageVector,
    title: String,
    description: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
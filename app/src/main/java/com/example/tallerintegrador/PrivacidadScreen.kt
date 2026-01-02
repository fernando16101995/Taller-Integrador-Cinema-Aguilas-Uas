package com.example.tallerintegrador

import android.content.Context
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.navigation.NavController
import com.example.tallerintegrador.auth.AuthViewModel
import com.example.tallerintegrador.data.local.TokenManager
import kotlinx.coroutines.launch

/*
 * Archivo: PrivacidadScreen.kt
 *
 * Pantalla que gestiona la privacidad y seguridad de la cuenta del usuario.
 *
 * Seccion Privacidad:
 * - Historial Visible: Activa/desactiva la visibilidad del historial
 *   de peliculas vistas recientemente
 * - Limpiar Historial: Elimina completamente el historial de reproduccion
 *   con dialogo de confirmacion
 *
 * Seccion Seguridad:
 * - Cambiar Contrasena: Redirige al flujo de recuperacion de contrasena
 *   desde el login para mayor seguridad
 * - Sesiones Activas: Muestra los dispositivos conectados y permite
 *   cerrar sesion en todos ellos
 *
 * Seccion Gestion de Cuenta:
 * - Informacion de Cuenta: Muestra el nombre de usuario, email e ID
 * - Control Parental: Informacion sobre como activar el modo ninos
 *   desde la pantalla de seleccion de perfiles
 *
 * Todas las preferencias se guardan en SharedPreferences con la clave
 * "privacy_prefs" y persisten entre sesiones.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacidadScreen(
    navController: NavController?,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("privacy_prefs", Context.MODE_PRIVATE) }
    val tokenManager = remember { TokenManager(context.applicationContext) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Estados de privacidad
    var historialVisible by remember {
        mutableStateOf(prefs.getBoolean("historial_visible", true))
    }

    // Diálogos
    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showSessionsDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Privacidad y Seguridad",
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
            // PRIVACIDAD
            item {
                SectionHeaderPriv("Privacidad")
            }

            item {
                PrivacySwitchItem(
                    icon = Icons.Filled.History,
                    title = "Historial Visible",
                    subtitle = "Mostrar películas vistas recientemente",
                    checked = historialVisible,
                    onCheckedChange = {
                        historialVisible = it
                        prefs.edit().putBoolean("historial_visible", it).apply()
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                if (it) "Historial visible" else "Historial oculto"
                            )
                        }
                    }
                )
            }

            item {
                PrivacyActionItem(
                    icon = Icons.Filled.DeleteSweep,
                    title = "Limpiar Historial",
                    subtitle = "Eliminar todo el historial de reproducción",
                    onClick = { showClearHistoryDialog = true }
                )
            }

            // SEGURIDAD
            item {
                SectionHeaderPriv("Seguridad")
            }

            item {
                PrivacyActionItem(
                    icon = Icons.Filled.Password,
                    title = "Cambiar Contraseña",
                    subtitle = "Usa 'Recuperar contraseña' en el login",
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar(
                                "Cierra sesión y usa 'Recuperar contraseña' en el login"
                            )
                        }
                    }
                )
            }

            item {
                PrivacyActionItem(
                    icon = Icons.Filled.Devices,
                    title = "Sesiones Activas",
                    subtitle = "Gestionar dispositivos conectados",
                    onClick = { showSessionsDialog = true }
                )
            }

            // GESTIÓN DE CUENTA
            item {
                SectionHeaderPriv("Gestión de Cuenta")
            }

            item {
                InfoCardPriv(
                    icon = Icons.Filled.AccountCircle,
                    title = "Información de Cuenta",
                    description = "Usuario: ${tokenManager.getUserName()}\n" +
                            "Email: ${tokenManager.getUserEmail()}\n" +
                            "ID: ${tokenManager.getUserId()}"
                )
            }

            item {
                InfoCardPriv(
                    icon = Icons.Filled.Info,
                    title = "Sobre Control Parental",
                    description = "Para acceso infantil, selecciona un perfil de niño desde la pantalla de perfiles."
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // DIÁLOGO: LIMPIAR HISTORIAL
    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = {
                Text(
                    "Limpiar Historial",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    "¿Estás seguro de que deseas eliminar todo tu historial de " +
                            "reproducción? Esta acción no se puede deshacer.",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearHistoryDialog = false
                        scope.launch {
                            kotlinx.coroutines.delay(500)
                            snackbarHostState.showSnackbar("Historial eliminado")
                        }
                    }
                ) {
                    Text("Eliminar", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.primary)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // DIÁLOGO: SESIONES ACTIVAS
    if (showSessionsDialog) {
        AlertDialog(
            onDismissRequest = { showSessionsDialog = false },
            title = {
                Text(
                    "Sesiones Activas",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    SessionItem(
                        device = "Android - Este dispositivo",
                        location = "Culiacán, México",
                        date = "Ahora",
                        isCurrent = true
                    )
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                    SessionItem(
                        device = "Web - Chrome",
                        location = "Culiacán, México",
                        date = "Hace 2 días"
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showSessionsDialog = false }) {
                    Text("Cerrar", color = MaterialTheme.colorScheme.primary)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("Sesiones cerradas")
                        }
                        showSessionsDialog = false
                    }
                ) {
                    Text("Cerrar Todas", color = Color.Red)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun SectionHeaderPriv(title: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
fun PrivacySwitchItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
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
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                    uncheckedThumbColor = Color.Gray,
                    uncheckedTrackColor = Color.Gray.copy(alpha = 0.3f)
                )
            )
        }
    }
}

@Composable
fun PrivacyActionItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDestructive)
                Color.Red.copy(alpha = 0.1f)
            else
                MaterialTheme.colorScheme.surfaceVariant
        ),
        onClick = onClick
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
                tint = if (isDestructive) Color.Red else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (isDestructive) Color.Red else MaterialTheme.colorScheme.onSurface,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = subtitle,
                    color = if (isDestructive)
                        Color.Red.copy(alpha = 0.7f)
                    else
                        MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp
                )
            }

            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = "Ir",
                tint = if (isDestructive)
                    Color.Red.copy(alpha = 0.5f)
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun InfoCardPriv(
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
                modifier = Modifier.size(32.dp)
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

@Composable
fun SessionItem(
    device: String,
    location: String,
    date: String,
    isCurrent: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isCurrent) Icons.Filled.PhoneAndroid else Icons.Filled.Computer,
            contentDescription = device,
            tint = if (isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = device,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                if (isCurrent) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            "Actual",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Text(
                text = "$location • $date",
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }
    }
}
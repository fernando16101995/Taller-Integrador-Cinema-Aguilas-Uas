package com.example.tallerintegrador

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tallerintegrador.ui.theme.DarkBlue
import com.example.tallerintegrador.ui.theme.Yellow

@Composable
fun PerfilScreen(navController: NavController?) {
    var showLogoutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBlue)
    ) {
        // Header del perfil
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Yellow.copy(alpha = 0.1f))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Yellow),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = "Avatar",
                    tint = DarkBlue,
                    modifier = Modifier.size(60.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Usuario Demo",
                color = Yellow,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "usuario@demo.com",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Estadísticas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatItem(label = "Vistas", value = "42")
                VerticalDivider(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp),
                    color = Color.White.copy(alpha = 0.3f)
                )
                StatItem(label = "Favoritas", value = "3")
                VerticalDivider(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp),
                    color = Color.White.copy(alpha = 0.3f)
                )
                StatItem(label = "Listas", value = "5")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Opciones del perfil
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                ProfileOption(
                    icon = Icons.Filled.Person,
                    title = "Editar Perfil",
                    subtitle = "Actualiza tu información personal",
                    onClick = { /* TODO: Editar perfil */ }
                )
            }

            item {
                ProfileOption(
                    icon = Icons.Filled.Notifications,
                    title = "Notificaciones",
                    subtitle = "Configura tus preferencias",
                    onClick = { /* TODO: Notificaciones */ }
                )
            }

            item {
                ProfileOption(
                    icon = Icons.Filled.Lock,
                    title = "Privacidad y Seguridad",
                    subtitle = "Gestiona tu cuenta",
                    onClick = { /* TODO: Privacidad */ }
                )
            }

            item {
                ProfileOption(
                    icon = Icons.Filled.Settings,
                    title = "Configuración",
                    subtitle = "Ajustes de la aplicación",
                    onClick = { /* TODO: Configuración */ }
                )
            }

            item {
                ProfileOption(
                    icon = Icons.Filled.Info,
                    title = "Acerca de",
                    subtitle = "Versión 1.0.0",
                    onClick = { /* TODO: Acerca de */ }
                )
            }

            item {
                ProfileOption(
                    icon = Icons.AutoMirrored.Filled.ExitToApp, // <--- Esta es la versión correcta
                    title = "Cerrar Sesión",
                    subtitle = "Salir de tu cuenta",
                    onClick = { showLogoutDialog = true },
                    isDestructive = true
                )
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    // Diálogo de confirmación de cierre de sesión
    if (showLogoutDialog) {
        // --- CÓDIGO NUEVO Y CORRECTO ---
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Cerrar Sesión",
                    color = Yellow,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "¿Estás seguro que deseas cerrar sesión?",
                    color = Color.White
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        navController?.navigate("welcome") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                ) {
                    Text("Cerrar Sesión", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar", color = Yellow)
                }
            },
            containerColor = DarkBlue, // <--- El parámetro correcto es 'containerColor'
            shape = RoundedCornerShape(16.dp)
        )

    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            color = Yellow,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.7f),
            fontSize = 14.sp
        )
    }
}

@Composable
fun ProfileOption(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.05f) // 'backgroundColor' ahora es 'containerColor'
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp // 'elevation' se configura así
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(if (isDestructive) Color.Red.copy(alpha = 0.2f) else Yellow.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isDestructive) Color.Red else Yellow,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    color = if (isDestructive) Color.Red else Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Ir",
                tint = Color.White.copy(alpha = 0.5f)
            )
        }
    }
}
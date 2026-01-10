package com.example.tallerintegrador.feature.profiles

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.tallerintegrador.data.model.ProfileDto
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

/**
 * Pantalla de selección de perfiles
 * Muestra una lista de perfiles de usuario disponibles para seleccionar
 * Al seleccionar un perfil, navega a la pantalla principal (Home)
 *
 * @param vm ViewModel que maneja la lógica de perfiles
 * @param onProfileSelected Callback que se ejecuta cuando se selecciona un perfil
 */
@Composable
fun ProfilesScreen(
    vm: ProfilesViewModel = hiltViewModel(),
    onProfileSelected: () -> Unit,
    onCreateProfile: () -> Unit,
    onEditProfile: (ProfileDto) -> Unit
) {
    // Observa el estado actual de los perfiles (loading, error, perfiles disponibles)
    val state by vm.uiState.collectAsState()

    // Efecto que se ejecuta al cargar la pantalla para obtener la lista de perfiles
    LaunchedEffect(Unit) {
        vm.load()
    }

    // Contenedor principal con fondo degradado
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color.Black.copy(alpha = 0.6f),
                        MaterialTheme.colorScheme.background.copy(alpha = 0.85f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        when {
            // Estado de carga: muestra indicador de progreso circular
            state.loading -> {
                CircularProgressIndicator(Modifier.align(Alignment.Center))
            }

            else -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    state.error?.let { message ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0x33E50914))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Info,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = message,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(onClick = { vm.clearError() }) {
                                    Icon(
                                        imageVector = Icons.Filled.Close,
                                        contentDescription = "Cerrar",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                    }

                    Spacer(Modifier.height(48.dp))

                    // Título de la pantalla
                    Text(
                        text = "¿Quién está viendo?",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(Modifier.height(48.dp))

                    // Grid de 2 columnas para mostrar los perfiles
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.profiles) { profile ->
                             // Tarjeta individual de perfil
                             Card(
                                 modifier = Modifier
                                     .fillMaxWidth()
                                     .clickable { vm.select(profile.id) { onProfileSelected() } },
                                 colors = CardDefaults.cardColors(
                                     containerColor = Color(0xFF2A2A2A)
                                 )
                             ) {
                                 Box(
                                     modifier = Modifier
                                         .fillMaxWidth()
                                         .padding(16.dp)
                                 ) {
                                     Column(
                                         modifier = Modifier.fillMaxWidth(),
                                         horizontalAlignment = Alignment.CenterHorizontally
                                     ) {
                                         // Avatar del perfil (circular)
                                         AsyncImage(
                                             model = profile.avatarUrl ?: "https://placehold.co/200/E50914/FFF?text=P",
                                             contentDescription = profile.nombre,
                                             modifier = Modifier.size(96.dp).clip(CircleShape)
                                         )

                                         Spacer(Modifier.height(12.dp))

                                         // Nombre del perfil
                                         Text(
                                             text = profile.nombre,
                                             color = Color.White,
                                             fontWeight = FontWeight.SemiBold
                                         )

                                         // Indicador de perfil infantil
                                         if (profile.esNino) {
                                             Spacer(Modifier.height(4.dp))
                                             Text(
                                                 text = "NIÑOS",
                                                 color = Color(0xFF4CAF50),
                                                 style = MaterialTheme.typography.labelSmall
                                             )
                                         }
                                     }

                                     var menuExpanded by remember { mutableStateOf(false) }

                                     Box(modifier = Modifier.align(Alignment.TopEnd)) {
                                         IconButton(onClick = { menuExpanded = true }) {
                                             Icon(
                                                 imageVector = Icons.Filled.MoreVert,
                                                 contentDescription = "Más opciones",
                                                 tint = Color.White
                                             )
                                         }
                                         DropdownMenu(
                                             expanded = menuExpanded,
                                             onDismissRequest = { menuExpanded = false }
                                         ) {
                                             DropdownMenuItem(
                                                 text = { Text("Editar") },
                                                 onClick = {
                                                     menuExpanded = false
                                                     onEditProfile(profile)
                                                 }
                                             )
                                             DropdownMenuItem(
                                                 text = { Text("Eliminar", color = Color(0xFFE50914)) },
                                                 onClick = {
                                                     menuExpanded = false
                                                     vm.delete(profile.id)
                                                 }
                                             )
                                         }
                                     }
                                 }
                             }
                        }

                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onCreateProfile() },
                                colors = CardDefaults.cardColors(
                                    containerColor = Color.Transparent
                                ),
                                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f))
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Add,
                                        contentDescription = "Agregar perfil",
                                        tint = Color.White,
                                        modifier = Modifier.size(48.dp)
                                    )

                                    Spacer(Modifier.height(12.dp))

                                    Text(
                                        text = "Agregar perfil",
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

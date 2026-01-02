package com.example.tallerintegrador

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tallerintegrador.auth.AuthViewModel
import com.example.tallerintegrador.data.local.TokenManager
import kotlinx.coroutines.launch

/*
 * Archivo: EditarPerfilScreen.kt
 *
 * Pantalla que permite al usuario editar su informacion personal.
 *
 * Funcionalidades:
 * - Cambiar nombre de usuario: Edita el nombre mostrado en la app
 * - Cambiar correo electronico: Actualiza el email de la cuenta
 * - Cambiar avatar: Selecciona entre 6 avatares predefinidos
 *
 * Validaciones implementadas:
 * - Nombre: Minimo 3 caracteres, no puede estar vacio
 * - Email: Debe ser un formato valido de correo electronico
 *
 * Nota sobre cambio de contrasena:
 * Para cambiar la contrasena, el usuario debe usar la opcion
 * "Recuperar contrasena" desde la pantalla de inicio de sesion.
 * Esto garantiza un proceso seguro mediante codigo de verificacion por email.
 *
 * Todos los cambios se guardan en TokenManager y se actualizan
 * en la sesion actual del usuario.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarPerfilScreen(
    navController: NavController?,
    authViewModel: AuthViewModel
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context.applicationContext) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Estados de formulario
    var nombre by remember { mutableStateOf(tokenManager.getUserName() ?: "") }
    var email by remember { mutableStateOf(tokenManager.getUserEmail() ?: "") }

    var selectedAvatar by remember { mutableIntStateOf(
        context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            .getInt("avatar_id", 0)
    ) }

    var showAvatarDialog by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Validaciones
    val nombreError = remember(nombre) {
        when {
            nombre.isBlank() -> "El nombre no puede estar vacío"
            nombre.length < 3 -> "El nombre debe tener al menos 3 caracteres"
            else -> null
        }
    }

    val emailError = remember(email) {
        when {
            email.isBlank() -> "El email no puede estar vacío"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
                "Email inválido"
            else -> null
        }
    }

    val isFormValid = nombreError == null && emailError == null

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Editar Perfil",
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
                actions = {
                    TextButton(
                        onClick = { showConfirmDialog = true },
                        enabled = isFormValid && !isLoading
                    ) {
                        Text(
                            "Guardar",
                            color = if (isFormValid) MaterialTheme.colorScheme.primary else Color.Gray,
                            fontWeight = FontWeight.Bold
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
                .padding(horizontal = 16.dp)
        ) {
            item { Spacer(modifier = Modifier.height(24.dp)) }

            // AVATAR
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .border(
                                4.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                CircleShape
                            )
                            .clickable { showAvatarDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getAvatarIcon(selectedAvatar),
                            contentDescription = "Avatar",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(70.dp)
                        )

                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surface)
                                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Cambiar avatar",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        "Toca para cambiar avatar",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                        fontSize = 14.sp
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            // INFORMACIÓN PERSONAL
            item {
                Text(
                    "Información Personal",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Nombre
            item {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de usuario", color = MaterialTheme.colorScheme.primary) },
                    leadingIcon = {
                        Icon(Icons.Filled.Person, "Nombre", tint = MaterialTheme.colorScheme.primary)
                    },
                    isError = nombreError != null,
                    supportingText = {
                        nombreError?.let {
                            Text(it, color = Color.Red)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        errorBorderColor = Color.Red,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Email
            item {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico", color = MaterialTheme.colorScheme.primary) },
                    leadingIcon = {
                        Icon(Icons.Filled.Email, "Email", tint = MaterialTheme.colorScheme.primary)
                    },
                    isError = emailError != null,
                    supportingText = {
                        emailError?.let {
                            Text(it, color = Color.Red)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        errorBorderColor = Color.Red,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            // AVISO SOBRE CONTRASEÑA
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.Info,
                            contentDescription = "Info",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                "¿Olvidaste tu contraseña?",
                                color = MaterialTheme.colorScheme.onBackground,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Usa la opción 'Recuperar contraseña' en la pantalla de inicio de sesión para restablecerla de forma segura",
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }

            // BOTÓN GUARDAR
            item {
                Button(
                    onClick = { showConfirmDialog = true },
                    enabled = isFormValid && !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Icon(
                            Icons.Filled.Save,
                            contentDescription = "Guardar",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Guardar Cambios",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }

    // DIÁLOGO DE SELECCIÓN DE AVATAR
    if (showAvatarDialog) {
        AlertDialog(
            onDismissRequest = { showAvatarDialog = false },
            title = {
                Text(
                    "Selecciona tu avatar",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (i in 0..2) {
                            AvatarOption(
                                icon = getAvatarIcon(i),
                                isSelected = selectedAvatar == i,
                                onClick = {
                                    selectedAvatar = i
                                    context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                        .edit()
                                        .putInt("avatar_id", i)
                                        .apply()
                                    showAvatarDialog = false
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Avatar actualizado")
                                    }
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (i in 3..5) {
                            AvatarOption(
                                icon = getAvatarIcon(i),
                                isSelected = selectedAvatar == i,
                                onClick = {
                                    selectedAvatar = i
                                    context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                                        .edit()
                                        .putInt("avatar_id", i)
                                        .apply()
                                    showAvatarDialog = false
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Avatar actualizado")
                                    }
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAvatarDialog = false }) {
                    Text("Cerrar", color = MaterialTheme.colorScheme.primary)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // DIÁLOGO DE CONFIRMACIÓN
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = {
                Text(
                    "Confirmar cambios",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        "¿Deseas guardar los siguientes cambios?",
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    if (nombre != tokenManager.getUserName()) {
                        Text(
                            "• Nombre: $nombre",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp
                        )
                    }
                    if (email != tokenManager.getUserEmail()) {
                        Text(
                            "• Email: $email",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 14.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        isLoading = true

                        scope.launch {
                            try {
                                kotlinx.coroutines.delay(1500)

                                tokenManager.saveAuthData(
                                    token = tokenManager.getToken() ?: "",
                                    userId = tokenManager.getUserId(),
                                    userName = nombre,
                                    userEmail = email
                                )

                                snackbarHostState.showSnackbar(
                                    "Perfil actualizado exitosamente"
                                )

                                navController?.popBackStack()

                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar(
                                    "Error al guardar: ${e.message}"
                                )
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                ) {
                    Text("Guardar", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar", color = MaterialTheme.colorScheme.onSurface)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun getAvatarIcon(id: Int) = when(id) {
    0 -> Icons.Filled.Person
    1 -> Icons.Filled.Face
    2 -> Icons.Filled.AccountCircle
    3 -> Icons.Filled.Star
    4 -> Icons.Filled.Favorite
    5 -> Icons.Filled.EmojiEmotions
    else -> Icons.Filled.Person
}

@Composable
fun AvatarOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(
                if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.surfaceVariant
            )
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = "Avatar",
            tint = if (isSelected)
                MaterialTheme.colorScheme.onPrimary
            else
                MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(40.dp)
        )
    }
}
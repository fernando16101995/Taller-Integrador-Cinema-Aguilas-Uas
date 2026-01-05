package com.example.tallerintegrador.feature.subscription

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

/*
 * Archivo: SubscriptionScreen.kt
 *
 * Pantalla que se muestra despues de seleccionar un perfil para verificar
 * si el usuario tiene una suscripcion activa. Si no tiene, muestra el plan
 * de pago con Stripe.
 *
 * Flujo:
 * 1. Se inicializa y verifica automaticamente el estado de suscripcion
 * 2. Si tiene suscripcion activa: navega directo a Home
 * 3. Si NO tiene: muestra informacion del plan mensual ($9.90 USD)
 * 4. Usuario puede presionar "Suscribirse Ahora" para abrir Stripe
 * 5. Se abre el navegador con el formulario de pago de Stripe
 * 6. Despues del pago, el backend actualiza la suscripcion
 *
 * Componentes:
 * - SubscriptionScreen: Componente principal con logica de navegacion
 * - SubscriptionContent: Muestra la informacion del plan y caracteristicas
 * - PricingCard: Tarjeta con el precio destacado
 * - FeaturesSection: Lista de 6 caracteristicas incluidas
 * - LoadingContent: Indicador de carga animado
 * - ErrorMessage: Mensaje de error dismissible
 *
 * Nota: El boton "Continuar sin suscripcion" es temporal para desarrollo.
 */

/**
 * Pantalla de suscripción que se muestra después de seleccionar perfil
 * si el usuario no tiene una suscripción activa
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    navController: NavController,
    subscriptionViewModel: SubscriptionViewModel = hiltViewModel()
) {
    val uiState by subscriptionViewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Si el usuario ya tiene suscripción activa, navegar al catálogo
    LaunchedEffect(uiState.hasActiveSubscription) {
        if (uiState.hasActiveSubscription) {
            navController.navigate("home") {
                popUpTo("subscription") { inclusive = true }
            }
        }
    }

    // Manejar la URL de checkout cuando esté disponible
    LaunchedEffect(uiState.checkoutUrl) {
        uiState.checkoutUrl?.let { url ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Suscripción",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
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
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                LoadingContent()
            } else {
                SubscriptionContent(
                    onSubscribe = {
                        subscriptionViewModel.createCheckoutSession { url ->
                            // La URL se manejará en el LaunchedEffect
                        }
                    },
                    onSkip = {
                        // Por ahora permitimos pasar sin suscripción
                        navController.navigate("home") {
                            popUpTo("subscription") { inclusive = true }
                        }
                    },
                    error = uiState.error,
                    onDismissError = { subscriptionViewModel.clearError() }
                )
            }
        }
    }
}

/**
 * Contenido principal de la pantalla de suscripción
 */
@Composable
fun SubscriptionContent(
    onSubscribe: () -> Unit,
    onSkip: () -> Unit,
    error: String?,
    onDismissError: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Icono principal
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFE50914).copy(alpha = 0.3f),
                            Color(0xFFB20710).copy(alpha = 0.1f)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.CardMembership,
                contentDescription = "Suscripción",
                tint = Color(0xFFE50914),
                modifier = Modifier.size(64.dp)
            )
        }

        // Título
        Text(
            text = "Acceso Premium",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )

        // Subtítulo
        Text(
            text = "Disfruta de contenido ilimitado con nuestra suscripción mensual",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tarjeta de precio
        PricingCard()

        Spacer(modifier = Modifier.height(8.dp))

        // Características
        FeaturesSection()

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de suscribirse
        Button(
            onClick = onSubscribe,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFE50914)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                Icons.Filled.Lock,
                contentDescription = null,
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Suscribirse Ahora",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        // Botón para omitir (temporal)
        TextButton(
            onClick = onSkip,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                "Continuar sin suscripción",
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        // Mensaje de error
        error?.let { errorMessage ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.Error,
                        contentDescription = "Error",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        errorMessage,
                        modifier = Modifier.weight(1f),
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                    IconButton(onClick = onDismissError) {
                        Icon(
                            Icons.Filled.Close,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

/**
 * Tarjeta con el precio de la suscripción
 */
@Composable
fun PricingCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Plan Mensual",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    "$",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE50914)
                )
                Text(
                    "9.90",
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE50914)
                )
                Text(
                    "USD",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            Text(
                "por mes",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * Sección con las características incluidas
 */
@Composable
fun FeaturesSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Características incluidas:",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )

        FeatureItem(
            icon = Icons.Filled.CheckCircle,
            text = "Acceso ilimitado a todo el catálogo"
        )

        FeatureItem(
            icon = Icons.Filled.CheckCircle,
            text = "Sin anuncios ni interrupciones"
        )

        FeatureItem(
            icon = Icons.Filled.CheckCircle,
            text = "Calidad de video HD"
        )

        FeatureItem(
            icon = Icons.Filled.CheckCircle,
            text = "Hasta 5 perfiles personalizados"
        )

        FeatureItem(
            icon = Icons.Filled.CheckCircle,
            text = "Contenido exclusivo"
        )

        FeatureItem(
            icon = Icons.Filled.CheckCircle,
            text = "Cancela cuando quieras"
        )
    }
}

/**
 * Item individual de característica
 */
@Composable
fun FeatureItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF4CAF50),
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
        )
    }
}

/**
 * Indicador de carga
 */
@Composable
fun LoadingContent() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        val infiniteTransition = rememberInfiniteTransition(label = "loading")
        val rotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing)
            ),
            label = "rotation"
        )

        Icon(
            imageVector = Icons.Filled.Refresh,
            contentDescription = "Cargando",
            tint = Color(0xFFE50914),
            modifier = Modifier
                .size(64.dp)
                .rotate(rotation)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Verificando suscripción...",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 16.sp
        )
    }
}


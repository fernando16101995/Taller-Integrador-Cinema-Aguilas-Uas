package com.example.tallerintegrador

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.net.toUri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tallerintegrador.feature.peliculas.PeliculaViewModel
import com.example.tallerintegrador.ui.theme.DarkBlue
import com.example.tallerintegrador.ui.theme.Yellow
@OptIn(ExperimentalMaterial3Api::class) // <-- AÑADE ESTA LÍNEA
@Composable
fun DetallePeliculaScreen(
    peliculaTitulo: String,
    viewModel: PeliculaViewModel,
    navController: NavController
) {
    val peliculas by viewModel.peliculas.collectAsState()
    var isFavorite by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (peliculas.isEmpty()) {
            viewModel.getPeliculas()
        }
    }

    val pelicula = peliculas.find { it.titulo == peliculaTitulo }

    if (pelicula == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBlue),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Yellow)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                // La nueva API para colores y elevación
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent, // Color de fondo
                    navigationIconContentColor = Color.White, // Color del icono de navegación
                    actionIconContentColor = Color.White // Color de los iconos de acción
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            // tint ya no es necesario aquí, se controla con 'navigationIconContentColor'
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isFavorite = !isFavorite }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = if (isFavorite) Color.Red else Color.White // Tint aquí aún es útil para el cambio de color dinámico
                        )
                    }
                    IconButton(onClick = { /* Compartir */ }) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Compartir"
                            // tint ya no es necesario
                        )
                    }
                }
            )
        },
        containerColor = DarkBlue // <-- El parámetro correcto para el fondo
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
        ) {
            // Póster con gradiente
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
            ) {
                AsyncImage(
                    model = pelicula.posterUrl,
                    contentDescription = pelicula.titulo,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )


                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    DarkBlue.copy(alpha = 0.7f),
                                    DarkBlue
                                ),
                                startY = 200f
                            )
                        )
                )
            }

            // Información de la película
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = pelicula.titulo,
                    color = Yellow,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InfoChip(text = pelicula.genero)
                    InfoChip(text = "${pelicula.duracion} min")
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Sinopsis",
                    color = Yellow,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = pelicula.sinopsis,
                    color = Color.White,
                    fontSize = 16.sp,
                    lineHeight = 24.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Reproductor de YouTube
                Text(
                    text = "Tráiler",
                    color = Yellow,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                val context = LocalContext.current

                Button(
                    onClick = {
                        val url = pelicula.videoUrl
                        val customTabsIntent = CustomTabsIntent.Builder().build()
                        customTabsIntent.launchUrl(context, url.toUri())
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Yellow),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Ver tráiler",
                        tint = DarkBlue
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ver tráiler",
                        color = DarkBlue,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }


                Spacer(modifier = Modifier.height(32.dp))

                // Botón de reproducir
                // --- CÓDIGO NUEVO Y CORRECTO ---
                Button(
                    onClick = { /* Reproducir película */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Yellow), // <--- El parámetro correcto es 'containerColor'
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PlayArrow,
                        contentDescription = "Reproducir",
                        tint = DarkBlue,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Reproducir película",
                        color = DarkBlue,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun InfoChip(text: String) {
    Surface(
        color = Yellow.copy(alpha = 0.2f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            color = Yellow,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubePlayer(videoUrl: String) {
    val videoId = remember(videoUrl) { extractYouTubeVideoId(videoUrl) }
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black // 'backgroundColor' ahora es 'containerColor'
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp // 'elevation' ahora se configura así
        )
    ) {
        // --- CÓDIGO NUEVO Y CORRECTO ---
        if (videoId != null) {
            // Si tenemos un ID, mostramos el reproductor
            AndroidView(
                factory = {
                    WebView(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        webChromeClient = WebChromeClient()

                        val embedUrl = "https://www.youtube.com/embed/$videoId"
                        loadUrl(embedUrl)
                    }
                },
                update = { webView ->
                    val embedUrl = "https://www.youtube.com/embed/$videoId"
                    webView.loadUrl(embedUrl)
                },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            // Si NO tenemos un ID, mostramos el mensaje de error
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tráiler no disponible",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 16.sp
                )
            }
        }
}
    }
fun extractYouTubeVideoId(url: String): String? {
    return try {
        when {
            url.contains("youtube.com/watch?v=") -> {
                url.substringAfter("watch?v=").substringBefore("&")
            }
            url.contains("youtu.be/") -> {
                url.substringAfter("youtu.be/").substringBefore("?")
            }
            url.contains("youtube.com/embed/") -> {
                url.substringAfter("embed/").substringBefore("?")
            }
            else -> null
        }
    } catch (e: Exception) {
        null
    }
}
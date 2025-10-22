package com.example.tallerintegrador

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.tallerintegrador.ui.theme.DarkBlue
import com.example.tallerintegrador.ui.theme.TallerIntegradorTheme
import com.example.tallerintegrador.ui.theme.Yellow
import com.example.tallerintegrador.R

@Composable
fun WelcomeScreen(navController: NavController) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Fondo con capa oscura
        Image(
            painter = painterResource(id = R.drawable.login_background), // Corregido
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        )

        // Contenido de la pantalla
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // TopBar - solo con botones a la derecha
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End, // Alineado a la derecha
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { navController.navigate("login") },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Yellow)
                ) {
                    Text("Iniciar sesión", color = DarkBlue)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { navController.navigate("register") }, // Acción añadida
                    colors = ButtonDefaults.buttonColors(backgroundColor = Yellow)
                ) {
                    Text("Registrarse", color = DarkBlue)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))

            // Contenido Principal
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tu cine en casa, fácil y rápido",
                    color = Yellow,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Disfruta de tus películas y series favoritas en cualquier dispositivo",
                    color = Yellow,
                    fontWeight = FontWeight.Normal,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(60.dp)) // Espacio ajustado

                // Título movido al centro
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "CINEMA ÁGUILAS UAS",
                        color = Yellow,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "UNIVERSIDAD AUTÓNOMA DE SINALOA",
                        color = Yellow.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(60.dp)) // Espacio ajustado

                // Botones de acción
                Button(
                    onClick = { navController.navigate("login") },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Yellow),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(50.dp)
                ) {
                    Text("Comenzar ahora", color = DarkBlue, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { /* TODO: Ver catálogo */ },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Yellow),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(50.dp)
                ) {
                    Text("Ver catálogo", color = DarkBlue, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    TallerIntegradorTheme {
        WelcomeScreen(rememberNavController())
    }
}

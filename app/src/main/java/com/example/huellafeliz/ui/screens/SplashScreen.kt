package com.example.huellafeliz.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.example.huellafeliz.R
import com.example.huellafeliz.navigation.Screen
import com.example.huellafeliz.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val userProfile by authViewModel.usuarioLogueado.collectAsState()

    // Animación de escala y opacidad
    val scale by animateFloatAsState(
        targetValue    = 1f,
        animationSpec  = tween(800, easing = FastOutSlowInEasing),
        label          = "scale"
    )
    val alpha by animateFloatAsState(
        targetValue    = 1f,
        animationSpec  = tween(1000),
        label          = "alpha"
    )

    LaunchedEffect(Unit) {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            authViewModel.cargarPerfil()
        }
        delay(2000)
        
        val destino = if (currentUser == null) {
            Screen.Login.route
        } else {
            // Esperar un momento a que el perfil cargue para decidir
            if (userProfile?.rol == "refugio") Screen.PanelRefugio.route else Screen.Home.route
        }

        navController.navigate(destino) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(NaranjaHuellaFeliz, NaranjaOscuro)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo / ícono principal
            Image(
                painter = painterResource(id = R.drawable.imagen),
                contentDescription = "Logo",
                modifier = Modifier
                    .size(160.dp)
                    .scale(scale)
                    .alpha(alpha)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text       = "HuellaFeliz",
                fontSize   = 36.sp,
                fontWeight = FontWeight.Bold,
                color      = Blanco,
                modifier   = Modifier.alpha(alpha)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text     = "Catálogo de Mascotas en Adopción",
                fontSize = 14.sp,
                color    = Blanco.copy(alpha = 0.85f),
                modifier = Modifier.alpha(alpha)
            )

            Spacer(modifier = Modifier.height(60.dp))

            // Indicador de carga
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { i ->
                    val dotAlpha by animateFloatAsState(
                        targetValue   = 1f,
                        animationSpec = infiniteRepeatable(
                            animation  = tween(600, delayMillis = i * 200),
                            repeatMode = RepeatMode.Reverse
                        ),
                        label = "dot$i"
                    )
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .alpha(dotAlpha)
                            .background(Blanco, shape = androidx.compose.foundation.shape.CircleShape)
                    )
                }
            }
        }
    }
}

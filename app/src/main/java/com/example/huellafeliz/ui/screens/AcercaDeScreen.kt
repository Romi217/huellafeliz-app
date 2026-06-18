package com.example.huellafeliz.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.huellafeliz.R
import com.example.huellafeliz.ui.theme.*

@Composable
fun AcercaDeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(listOf(NaranjaHuellaFeliz, NaranjaOscuro))
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 12.dp)
            ) {
                IconButton(
                    onClick  = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(Icons.Default.ArrowBack, null, tint = Blanco)
                }
                Text(
                    text       = "Acerca de",
                    fontSize   = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Blanco,
                    modifier   = Modifier.align(Alignment.Center)
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))

            // ── Logo / Ícono ─────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(NaranjaHuellaFeliz),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.imagen),
                    contentDescription = "Logo HuellaFeliz",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                "HuellaFeliz",
                fontSize   = 38.sp,
                fontWeight = FontWeight.Bold,
                color      = GrisTexto
            )
            Text(
                "Versión 1.0.0",
                fontSize = 19.sp,
                color    = GrisSubtexto
            )

            Spacer(Modifier.height(24.dp))

            // ── Misión ───────────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(containerColor = GrisFondo)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Nuestra misión",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 24.sp,
                        color      = NaranjaHuellaFeliz
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text      = "HuellaFeliz conecta mascotas en adopción con familias amorosas. " +
                                    "Centralizamos la información de refugios y particulares para " +
                                    "facilitar el proceso de adopción responsable.",
                        fontSize  = 21.sp,
                        color     = GrisTexto,
                        textAlign = TextAlign.Justify
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Equipo ───────────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape    = RoundedCornerShape(16.dp),
                colors   = CardDefaults.cardColors(containerColor = GrisFondo)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Equipo desarrollador",
                        fontWeight = FontWeight.SemiBold,
                        fontSize   = 24.sp,
                        color      = NaranjaHuellaFeliz
                    )
                    Spacer(Modifier.height(12.dp))

                    IntegranteItem("HE", "Hector Manuel Arce León",    "Scrum Master")
                    Spacer(Modifier.height(10.dp))
                    IntegranteItem("MC", "Miguel Carlos Carvajal",      "Product Owner / Dev")
                    Spacer(Modifier.height(10.dp))
                    IntegranteItem("RP", "Romina Poma Matías",           "Developer")
                }
            }

            Spacer(Modifier.height(32.dp))

            Text(
                text     = "Hecho con ❤️ en Bolivia · 2026",
                fontSize = 19.sp,
                color    = GrisSubtexto,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun IntegranteItem(iniciales: String, nombre: String, rol: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(NaranjaHuellaFeliz),
            contentAlignment = Alignment.Center
        ) {
            Text(iniciales, color = Blanco, fontWeight = FontWeight.Bold, fontSize = 21.sp)
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(nombre, fontWeight = FontWeight.SemiBold, fontSize = 21.sp, color = GrisTexto)
            Text(rol,    fontSize = 18.sp, color = GrisSubtexto)
        }
    }
}

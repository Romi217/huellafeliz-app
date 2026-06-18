package com.example.huellafeliz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.huellafeliz.navigation.Screen
import com.example.huellafeliz.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val userProfile by authViewModel.usuarioLogueado.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(Unit) {
        authViewModel.cargarPerfil()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", color = Blanco, fontWeight = FontWeight.Bold, fontSize = 30.sp) },
                navigationIcon = {
                    IconButton(onClick = { 
                        val destino = if(userProfile?.rol == "refugio") Screen.PanelRefugio.route else Screen.Home.route
                        navController.navigate(destino) { popUpTo(0) }
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Blanco)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NaranjaHuellaFeliz)
            )
        },
        bottomBar = {
            HuellaFelizBottomBar(navController, userProfile?.rol ?: "adoptante", "perfil")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(GrisFondo)
                .verticalScroll(scrollState)
        ) {
            // ── Cabecera Perfil ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(NaranjaHuellaFeliz)
                    .padding(bottom = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Blanco.copy(0.2f))
                            .border(4.dp, Blanco, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, null, modifier = Modifier.size(60.dp), tint = Blanco)
                    }
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = userProfile?.nombre ?: "Usuario",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Blanco
                    )
                    Text(
                        text = userProfile?.correo ?: "correo@ejemplo.com",
                        fontSize = 24.sp,
                        color = Blanco.copy(0.8f)
                    )
                }
            }

            // ── Opciones (Estilo Figma) ───────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-20).dp)
                    .clip(RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp))
                    .background(Blanco)
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ProfileOptionItem(Icons.Default.Person, "Mi Información", "Nombre, correo, teléfono") { }
                ProfileOptionItem(Icons.Default.Home, "Mi Ciudad", userProfile?.ciudad ?: "Bolivia") { }
                ProfileOptionItem(Icons.AutoMirrored.Filled.Assignment, "Mis Solicitudes", "Ver estado de tus solicitudes") {
                    navController.navigate(if(userProfile?.rol == "refugio") Screen.Solicitudes.route else Screen.MisSolicitudes.route)
                }
                ProfileOptionItem(Icons.Default.Favorite, "Mis Favoritos", "Tus mascotas guardadas") {
                    navController.navigate(Screen.Favoritos.route)
                }
                ProfileOptionItem(Icons.Default.Info, "Acerca de", "Versión 1.0.0") {
                    navController.navigate(Screen.AcercaDe.route)
                }

                Spacer(Modifier.height(20.dp))

                // Botón Cerrar Sesión (Rojo Figma)
                OutlinedButton(
                    onClick = {
                        authViewModel.cerrarSesion()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, RojoError),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = RojoError)
                ) {
                    Icon(Icons.Default.Logout, null)
                    Spacer(Modifier.width(12.dp))
                    Text("Cerrar Sesión", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                }
            }
        }
    }
}

@Composable
fun ProfileOptionItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = GrisFondo.copy(0.5f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = NaranjaHuellaFeliz.copy(0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, null, tint = NaranjaHuellaFeliz, modifier = Modifier.size(20.dp))
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = GrisTexto)
                Text(subtitle, fontSize = 19.sp, color = GrisSubtexto)
            }
            Icon(Icons.Default.ChevronRight, null, tint = GrisSubtexto)
        }
    }
}

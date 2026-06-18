package com.example.huellafeliz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.huellafeliz.data.repository.SolicitudRepository
import com.example.huellafeliz.navigation.Screen
import com.example.huellafeliz.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisSolicitudesScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val repo = SolicitudRepository()
    val repoMascotas = com.example.huellafeliz.data.repository.MascotaRepository()
    val userProfile by authViewModel.usuarioLogueado.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.cargarPerfil()
    }
    
    val solicitudes by if (userProfile != null) {
        repo.getMisSolicitudes(userProfile!!.uid).collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList()) }
    }

    val misPublicaciones by if (userProfile != null) {
        repoMascotas.getMascotasByPublicador(userProfile!!.uid).collectAsState(initial = emptyList())
    } else {
        remember { mutableStateOf(emptyList()) }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(NaranjaHuellaFeliz).statusBarsPadding()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Blanco)
                    }
                }
                Text(
                    text = "Mis Solicitudes",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Blanco,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
                Text(
                    text = "${solicitudes.size + misPublicaciones.size} gestiones en total",
                    fontSize = 19.sp,
                    color = Blanco.copy(alpha = 0.9f),
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 4.dp)
                )
                Spacer(Modifier.height(16.dp))
            }
        },
        bottomBar = {
            HuellaFelizBottomBar(navController, userProfile?.rol ?: "adoptante", "mis_solicitudes")
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(GrisFondo),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (misPublicaciones.isNotEmpty()) {
                item {
                    Text("Publicaciones para Adopción", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = GrisTexto, modifier = Modifier.padding(vertical = 8.dp))
                }
                items(misPublicaciones) { m ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { 
                            navController.navigate(Screen.Detalle.createRoute(m.id))
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Blanco)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = m.fotoUrl,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(16.dp)),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text(m.nombre, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                                Text("Publicación de mascota", fontSize = 17.sp, color = GrisSubtexto)
                                Spacer(Modifier.height(8.dp))
                                // Estado de aprobación
                                Surface(
                                    color = (if(m.aprobada) VerdeDisponible else Color(0xFFFFA000)).copy(0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        if(m.aprobada) "✓ Aprobada y Visible" else "🕒 Pendiente de aprobación",
                                        color = if(m.aprobada) VerdeDisponible else Color(0xFFFFA000),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (solicitudes.isNotEmpty()) {
                item {
                    Text("Solicitudes que envié", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = GrisTexto, modifier = Modifier.padding(vertical = 8.dp))
                }
                items(solicitudes) { sol ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { 
                            navController.navigate(Screen.Detalle.createRoute(sol.idMascota))
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Blanco),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Imagen de la mascota
                            Box(
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(GrisFondo),
                                contentAlignment = Alignment.Center
                            ) {
                                if (sol.fotoMascota.isNotEmpty()) {
                                    AsyncImage(
                                        model = sol.fotoMascota,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop,
                                        alignment = Alignment.TopCenter
                                    )
                                } else {
                                    Icon(Icons.Default.Pets, null, tint = NaranjaHuellaFeliz)
                                }
                            }

                            Spacer(Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = sol.nombreMascota,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp,
                                        color = GrisTexto
                                    )
                                    Text(
                                        text = "Enviada: ${formatDate(sol.fecha)}",
                                        fontSize = 13.sp,
                                        color = GrisSubtexto
                                    )
                                }
                                
                                Text(
                                    text = sol.razaMascota.ifEmpty { "Raza no especificada" },
                                    fontSize = 19.sp,
                                    color = GrisSubtexto
                                )
                                
                                val sourceText = if (sol.tipoPublicador == "refugio") "Refugio Patitas Felices" else "Particular - ${sol.nombrePublicador}"
                                Text(
                                    text = sourceText,
                                    fontSize = 17.sp,
                                    color = GrisSubtexto.copy(alpha = 0.8f)
                                )
                                
                                Spacer(Modifier.height(8.dp))
                                
                                // Badge de Estado
                                StatusBadge(sol.estado)
                            }
                            
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = GrisSubtexto,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(estado: String) {
    val (color, icon, text) = when (estado.lowercase()) {
        "aceptado" -> Triple(VerdeDisponible, Icons.Default.CheckCircle, "Aceptada")
        "rechazado" -> Triple(RojoError, Icons.Default.Cancel, "Rechazada")
        else -> Triple(Color(0xFFFFA000), Icons.Default.PendingActions, "Pendiente")
    }
    
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = color, modifier = Modifier.size(14.dp))
            Spacer(Modifier.width(4.dp))
            Text(
                text = text,
                color = color,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

package com.example.huellafeliz.ui.screens

import android.widget.Toast
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.huellafeliz.data.model.Mascota
import com.example.huellafeliz.data.model.Solicitud
import com.example.huellafeliz.data.model.Usuario
import com.example.huellafeliz.data.repository.AuthRepository
import com.example.huellafeliz.data.repository.MascotaRepository
import com.example.huellafeliz.data.repository.SolicitudRepository
import com.example.huellafeliz.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleSolicitudScreen(navController: NavController, solicitudId: String) {
    val repoSol = SolicitudRepository()
    val repoAuth = AuthRepository()
    val repoMascota = MascotaRepository()
    
    val solicitud by repoSol.getSolicitudByIdFlow(solicitudId).collectAsState(initial = null)
    var adoptante by remember { mutableStateOf<Usuario?>(null) }
    var mascota by remember { mutableStateOf<Mascota?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Tiempo de espera para evitar carga infinita
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(8000)
        if (solicitud == null) {
            Toast.makeText(context, "Error al cargar los datos", Toast.LENGTH_SHORT).show()
            navController.popBackStack()
        }
    }

    LaunchedEffect(solicitud) {
        solicitud?.let { sol ->
            if (adoptante == null) {
                val resUser = repoAuth.getPerfil(sol.idAdoptante)
                if (resUser.isSuccess) adoptante = resUser.getOrNull()
            }
            if (mascota == null) {
                mascota = repoMascota.getMascotaById(sol.idMascota)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle de Solicitud", color = Blanco, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Blanco)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NaranjaHuellaFeliz)
            )
        }
    ) { padding ->
        if (solicitud == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = NaranjaHuellaFeliz)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(GrisFondo)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Info Mascota
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Blanco)
                ) {
                    Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(
                            model = solicitud!!.fotoMascota,
                            contentDescription = null,
                            modifier = Modifier.size(150.dp).clip(RoundedCornerShape(20.dp)),
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.TopCenter
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(solicitud!!.nombreMascota, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = GrisTexto)
                        Text("${solicitud!!.razaMascota}${if(mascota != null) " • " + mascota!!.edad else ""}", fontSize = 20.sp, color = GrisSubtexto)
                        
                        if (mascota != null) {
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                HealthBadgeMini("Tamaño: ${mascota!!.tamaño}")
                                if (mascota!!.vacunado) HealthBadgeMini("Vacunado")
                                if (mascota!!.castrado) HealthBadgeMini("Castrado")
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = mascota!!.descripcion,
                                fontSize = 16.sp,
                                color = GrisSubtexto,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 10.dp)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Info Adoptante
                Text("Información del Solicitante", modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), fontWeight = FontWeight.Bold, fontSize = 22.sp, color = GrisTexto)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Blanco)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        InfoRow(Icons.Default.Person, "Nombre", adoptante?.nombre ?: solicitud!!.nombreAdoptante)
                        if (adoptante != null) {
                            InfoRow(Icons.Default.Email, "Correo", adoptante!!.correo)
                            InfoRow(Icons.Default.Phone, "Teléfono", adoptante!!.telefono)
                            InfoRow(Icons.Default.LocationOn, "Ciudad", adoptante!!.ciudad)
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                // Estado y Acciones
                Text("Estado: ${solicitud!!.estado.uppercase()}", 
                    fontSize = 22.sp, 
                    fontWeight = FontWeight.Bold,
                    color = if(solicitud!!.estado == "pendiente") NaranjaHuellaFeliz else if(solicitud!!.estado == "aceptado") VerdeDisponible else RojoError
                )

                if (solicitud!!.estado == "pendiente") {
                    Spacer(Modifier.height(24.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Button(
                            onClick = {
                                scope.launch {
                                    val resSol = repoSol.actualizarEstadoSolicitud(solicitudId, "aceptado")
                                    if (resSol.isSuccess) {
                                        solicitud?.idMascota?.let { mid ->
                                            val resMas = repoMascota.actualizarEstado(mid, "Adoptado")
                                            if (resMas.isSuccess) {
                                                Toast.makeText(context, "¡Solicitud aceptada y mascota adoptada!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(context, "Solicitud aceptada, pero error al marcar mascota", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        Toast.makeText(context, "Error al aceptar la solicitud", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f).height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = VerdeDisponible),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Check, null, tint = Blanco)
                            Spacer(Modifier.width(8.dp))
                            Text("Aceptar", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Blanco)
                        }

                        Button(
                            onClick = {
                                scope.launch {
                                    repoSol.actualizarEstadoSolicitud(solicitudId, "rechazado")
                                    Toast.makeText(context, "Solicitud Rechazada", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f).height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = RojoError),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Close, null, tint = Blanco)
                            Spacer(Modifier.width(8.dp))
                            Text("Rechazar", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Blanco)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HealthBadgeMini(text: String) {
    Surface(
        color = NaranjaHuellaFeliz.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            color = NaranjaHuellaFeliz,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(Modifier.padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = NaranjaHuellaFeliz, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 14.sp, color = GrisSubtexto)
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = GrisTexto)
        }
    }
}

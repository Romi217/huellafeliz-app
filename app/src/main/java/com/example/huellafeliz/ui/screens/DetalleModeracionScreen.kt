package com.example.huellafeliz.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import com.example.huellafeliz.data.model.Usuario
import com.example.huellafeliz.data.repository.AuthRepository
import com.example.huellafeliz.data.repository.MascotaRepository
import com.example.huellafeliz.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleModeracionScreen(navController: NavController, mascotaId: String) {
    val repoMascota = MascotaRepository()
    val repoAuth = AuthRepository()
    var mascota by remember { mutableStateOf<Mascota?>(null) }
    var publicador by remember { mutableStateOf<Usuario?>(null) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(mascotaId) {
        mascota = repoMascota.getMascotaById(mascotaId)
        mascota?.let {
            val res = repoAuth.getPerfil(it.publicadoPor)
            if (res.isSuccess) publicador = res.getOrNull()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Revisar Publicación", color = Blanco, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Blanco)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = NaranjaHuellaFeliz)
            )
        }
    ) { padding ->
        if (mascota == null) {
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
                            model = mascota!!.fotoUrl,
                            contentDescription = null,
                            modifier = Modifier.size(180.dp).clip(RoundedCornerShape(20.dp)),
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.TopCenter
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(mascota!!.nombre, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold, color = GrisTexto)
                        Text("${mascota!!.especie} • ${mascota!!.raza} • ${mascota!!.edad}", fontSize = 20.sp, color = GrisSubtexto)
                        
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            HealthBadgeMini("Tamaño: ${mascota!!.tamaño}")
                            if (mascota!!.vacunado) HealthBadgeMini("Vacunado")
                            if (mascota!!.castrado) HealthBadgeMini("Castrado")
                        }
                        
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = mascota!!.descripcion,
                            fontSize = 18.sp,
                            color = GrisTexto,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        if (mascota!!.caracter.isNotBlank()) {
                            Spacer(Modifier.height(12.dp))
                            Text("Carácter:", fontWeight = FontWeight.Bold, color = GrisTexto)
                            Text(mascota!!.caracter, fontSize = 16.sp, color = GrisSubtexto, textAlign = TextAlign.Center)
                        }

                        if (mascota!!.requisitos.isNotBlank()) {
                            Spacer(Modifier.height(12.dp))
                            Text("Requisitos:", fontWeight = FontWeight.Bold, color = GrisTexto)
                            Text(mascota!!.requisitos, fontSize = 16.sp, color = GrisSubtexto, textAlign = TextAlign.Center)
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Info Publicador
                Text("Datos de quien da en adopción", modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), fontWeight = FontWeight.Bold, fontSize = 22.sp, color = GrisTexto)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Blanco)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        InfoRow(Icons.Default.Person, "Nombre", publicador?.nombre ?: "Usuario")
                        if (publicador != null) {
                            InfoRow(Icons.Default.Email, "Correo", publicador!!.correo)
                            InfoRow(Icons.Default.Phone, "Teléfono", publicador!!.telefono)
                            InfoRow(Icons.Default.LocationOn, "Ciudad", publicador!!.ciudad)
                        }
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Botones de Acción
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = {
                            scope.launch {
                                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                    .collection("mascotas").document(mascotaId).update("aprobada", true)
                                Toast.makeText(context, "Mascota Aprobada", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier.weight(1f).height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = VerdeDisponible),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Check, null, tint = Blanco)
                        Spacer(Modifier.width(8.dp))
                        Text("Aprobar", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Blanco)
                    }

                    Button(
                        onClick = {
                            scope.launch {
                                repoMascota.eliminarMascota(mascotaId)
                                Toast.makeText(context, "Publicación Rechazada/Eliminada", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
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
                Spacer(Modifier.height(40.dp))
            }
        }
    }
}

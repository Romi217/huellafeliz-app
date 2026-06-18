package com.example.huellafeliz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.TaskAlt
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
import com.example.huellafeliz.data.model.Solicitud
import com.example.huellafeliz.data.model.Mascota
import com.example.huellafeliz.navigation.Screen
import com.example.huellafeliz.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolicitudesScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val repo = SolicitudRepository()
    val repoMascota = com.example.huellafeliz.data.repository.MascotaRepository()
    val userProfile by authViewModel.usuarioLogueado.collectAsState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        authViewModel.cargarPerfil()
    }
    
    val solicitudesAdopcion by if (userProfile != null) {
        if (userProfile?.rol == "refugio") {
            // El administrador ve todas las solicitudes de adopción del sistema
            repo.getAllSolicitudes().collectAsState(initial = emptyList())
        } else {
            // Un particular (si llegara aquí) solo vería las suyas
            repo.getSolicitudesParaRefugio(userProfile!!.uid).collectAsState(initial = emptyList())
        }
    } else {
        remember { mutableStateOf(emptyList<Solicitud>()) }
    }

    val todasMascotas by repoMascota.getMascotas().collectAsState(initial = emptyList())
    val solicitudesPublicacion = todasMascotas.filter { !it.aprobada && it.tipoPublicador == "particular" }
    val publicacionesAprobadas = todasMascotas.filter { it.aprobada && it.tipoPublicador == "particular" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Buzón de Solicitudes", color = Blanco, fontWeight = FontWeight.Bold, fontSize = 28.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Blanco)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = NaranjaHuellaFeliz,
                    titleContentColor = Blanco,
                    navigationIconContentColor = Blanco
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).background(GrisFondo),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- SECCIÓN: PUBLICACIONES POR APROBAR ---
            if (solicitudesPublicacion.isNotEmpty()) {
                item {
                    SectionHeader("Nuevas Publicaciones (Pendientes)", NaranjaHuellaFeliz)
                }
                items(solicitudesPublicacion) { mascota ->
                    ModeracionCard(
                        mascota = mascota,
                        onClick = { navController.navigate(Screen.DetalleModeracion.createRoute(mascota.id)) },
                        onApprove = {
                            scope.launch {
                                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                    .collection("mascotas").document(mascota.id).update("aprobada", true)
                            }
                        }
                    )
                }
            }

            // --- SECCIÓN: SOLICITUDES DE ADOPCIÓN ---
            if (solicitudesAdopcion.isNotEmpty()) {
                item {
                    SectionHeader("Solicitudes de Adopción", NaranjaHuellaFeliz)
                }
                items(solicitudesAdopcion) { sol ->
                    SolicitudCard(
                        sol = sol, 
                        onClick = { navController.navigate(Screen.DetalleSolicitud.createRoute(sol.id)) },
                        onAction = { id, idMascota, nuevoEstado ->
                            scope.launch {
                                val res = repo.actualizarEstadoSolicitud(id, nuevoEstado)
                                if (res.isSuccess && nuevoEstado == "aceptado") {
                                    repoMascota.actualizarEstado(idMascota, "Adoptado")
                                }
                            }
                        }
                    )
                }
            }

            // --- SECCIÓN: PUBLICACIONES YA APROBADAS (HISTORIAL) ---
            if (publicacionesAprobadas.isNotEmpty()) {
                item {
                    SectionHeader("Publicaciones Aprobadas (Historial)", VerdeDisponible)
                }
                items(publicacionesAprobadas) { mascota ->
                    ModeracionCard(
                        mascota = mascota,
                        onClick = { navController.navigate(Screen.Detalle.createRoute(mascota.id)) },
                        onApprove = {},
                        isApproved = true
                    )
                }
            }

            if (solicitudesAdopcion.isEmpty() && solicitudesPublicacion.isEmpty() && publicacionesAprobadas.isEmpty()) {
                item {
                    Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay solicitudes pendientes", color = GrisSubtexto, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, color: Color) {
    Text(
        text = title,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
fun ModeracionCard(mascota: Mascota, onClick: () -> Unit, onApprove: () -> Unit, isApproved: Boolean = false) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = mascota.fotoUrl,
                contentDescription = null,
                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(mascota.nombre, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = GrisTexto)
                Text("Publicación de particular", fontSize = 18.sp, color = GrisSubtexto)
                if (isApproved) {
                    Text("✓ Publicado", color = VerdeDisponible, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
            if (!isApproved) {
                IconButton(
                    onClick = onApprove,
                    modifier = Modifier.background(VerdeDisponible.copy(0.1f), CircleShape)
                ) {
                    Icon(Icons.Default.Check, null, tint = VerdeDisponible)
                }
            } else {
                Icon(Icons.Default.TaskAlt, null, tint = VerdeDisponible)
            }
        }
    }
}

@Composable
fun SolicitudCard(sol: Solicitud, onClick: () -> Unit, onAction: (String, String, String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = sol.fotoMascota,
                contentDescription = null,
                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter
            )
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(sol.nombreMascota, fontWeight = FontWeight.Bold, fontSize = 24.sp, color = GrisTexto)
                Text("Adoptante: ${sol.nombreAdoptante}", fontSize = 19.sp, color = GrisSubtexto)
                val colorEstado = when(sol.estado.lowercase()) {
                    "aceptado" -> VerdeDisponible
                    "rechazado" -> RojoError
                    else -> NaranjaHuellaFeliz
                }
                Text(
                    text = sol.estado.uppercase(), 
                    fontSize = 17.sp, 
                    fontWeight = FontWeight.Bold,
                    color = colorEstado
                )
            }
            if (sol.estado == "pendiente") {
                Row {
                    IconButton(onClick = { onAction(sol.id, sol.idMascota, "aceptado") }) {
                        Icon(Icons.Default.Check, null, tint = VerdeDisponible, modifier = Modifier.size(32.dp))
                    }
                    IconButton(onClick = { onAction(sol.id, sol.idMascota, "rechazado") }) {
                        Icon(Icons.Default.Close, null, tint = RojoError, modifier = Modifier.size(32.dp))
                    }
                }
            }
        }
    }
}

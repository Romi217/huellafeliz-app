package com.example.huellafeliz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
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
import com.example.huellafeliz.data.repository.MascotaRepository
import com.example.huellafeliz.data.repository.SolicitudRepository
import com.example.huellafeliz.data.model.Mascota
import com.example.huellafeliz.data.model.Solicitud
import com.example.huellafeliz.navigation.Screen
import com.example.huellafeliz.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PanelRefugioScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel(),
    mascotaViewModel: MascotaViewModel = viewModel()
) {
    val repoMascotas = MascotaRepository()
    val repoSolicitudes = SolicitudRepository()
    val userProfile by authViewModel.usuarioLogueado.collectAsState()
    val scope = rememberCoroutineScope()

    // Estados de búsqueda y filtros
    val query by mascotaViewModel.searchQuery.collectAsState()
    val especieFiltro by mascotaViewModel.especieFiltro.collectAsState()
    val edadFiltro by mascotaViewModel.edadFiltro.collectAsState()
    val tamanoFiltro by mascotaViewModel.tamanoFiltro.collectAsState()
    val vac by mascotaViewModel.vacunadoFiltro.collectAsState()
    val cast by mascotaViewModel.castradoFiltro.collectAsState()
    val desp by mascotaViewModel.desparasitadoFiltro.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.cargarPerfil()
    }
    
    val misMascotasRaw by if (userProfile != null) repoMascotas.getMascotasByPublicador(userProfile!!.uid).collectAsState(initial = emptyList()) else remember { mutableStateOf(emptyList<Mascota>()) }
    val solicitudes by if (userProfile != null) repoSolicitudes.getSolicitudesParaRefugio(userProfile!!.uid).collectAsState(initial = emptyList()) else remember { mutableStateOf(emptyList<Solicitud>()) }
    val todasMascotas by repoMascotas.getMascotas().collectAsState(initial = emptyList())
    
    // Todas las mascotas disponibles para adopción (propias y aprobadas)
    val catalogoAdopcionFiltrado = remember(todasMascotas, query, especieFiltro, edadFiltro, tamanoFiltro, vac, cast, desp) {
        todasMascotas.filter { m ->
            val esVisible = m.aprobada || m.tipoPublicador == "refugio"
            val coincideBusqueda = m.nombre.contains(query, ignoreCase = true) || m.raza.contains(query, ignoreCase = true)
            val coincideEspecie = especieFiltro == "Todos" || m.especie.equals(especieFiltro, ignoreCase = true)
            val coincideEdad = edadFiltro == "Todos" || m.edad.equals(edadFiltro, ignoreCase = true)
            val coincideTamano = tamanoFiltro == "Todos" || m.tamaño.equals(tamanoFiltro, ignoreCase = true)
            val coincideVac = !vac || m.vacunado
            val coincideCast = !cast || m.castrado
            val coincideDesp = !desp || m.desparasitado
            
            esVisible && coincideBusqueda && coincideEspecie && coincideEdad && coincideTamano && coincideVac && coincideCast && coincideDesp
        }
    }

    val listaModeracion = todasMascotas.filter { !it.aprobada && it.tipoPublicador != "refugio" }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(NaranjaHuellaFeliz).statusBarsPadding()) {
                TopAppBar(
                    title = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Pets, null, tint = Blanco, modifier = Modifier.size(24.dp))
                                Spacer(Modifier.width(10.dp))
                                Text("Panel del Refugio", color = Blanco, fontWeight = FontWeight.ExtraBold, fontSize = 32.sp)
                                Spacer(Modifier.weight(1f))
                                IconButton(onClick = { /* Notificaciones */ }) {
                                    Icon(Icons.Default.Notifications, null, tint = Blanco, modifier = Modifier.size(28.dp))
                                }
                            }
                            Text(userProfile?.nombre ?: "Refugio Patitas Felices", color = Blanco.copy(0.9f), fontSize = 24.sp)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = NaranjaHuellaFeliz)
                )
                
                // Estadísticas
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem("${misMascotasRaw.size}", "Mis Mascotas")
                    StatItem("${solicitudes.filter { it.estado == "pendiente" }.size}", "Solicitudes")
                    StatItem("${listaModeracion.size}", "Por Aprobar")
                }

                // BARRA DE BÚSQUEDA Y FILTROS
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { mascotaViewModel.updateSearchQuery(it) },
                        placeholder = { Text("Buscar en el catálogo...", color = Blanco.copy(0.7f), fontSize = 20.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, null, tint = Blanco) },
                        singleLine = true,
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Blanco.copy(0.15f),
                            unfocusedContainerColor = Blanco.copy(0.1f),
                            focusedBorderColor = Blanco,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Blanco,
                            unfocusedTextColor = Blanco
                        )
                    )
                    
                    Surface(
                        onClick = { navController.navigate(Screen.Filtros.route) },
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Blanco.copy(0.2f)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Tune, null, tint = Blanco, modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }
        },
        bottomBar = {
            HuellaFelizBottomBar(navController, userProfile?.rol ?: "refugio", "panel_refugio")
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(GrisFondo).verticalScroll(rememberScrollState())) {
            
            // Catálogo Completo (Mascotas propias + Aprobadas)
            Text("Catálogo de Adopción", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp), color = GrisTexto)
            Box(Modifier.heightIn(max = 600.dp)) {
                ListaMascotasAdmin(
                    mascotas = catalogoAdopcionFiltrado,
                    onDelete = { id -> scope.launch { repoMascotas.eliminarMascota(id) } },
                    onEdit = { id -> navController.navigate(Screen.Publicar.createRoute(id)) },
                    onClick = { id -> navController.navigate(Screen.Detalle.createRoute(id)) },
                    uidUsuario = userProfile?.uid ?: ""
                )
            }

            if (listaModeracion.isNotEmpty()) {
                Text("Pendientes de aprobación", fontSize = 24.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp), color = NaranjaHuellaFeliz)
                Box(Modifier.heightIn(max = 400.dp)) {
                    ListaModeracionAdmin(
                        mascotas = listaModeracion,
                        onApprove = { id -> 
                            scope.launch { 
                                com.google.firebase.firestore.FirebaseFirestore.getInstance()
                                    .collection("mascotas").document(id).update("aprobada", true)
                            }
                        },
                        onClick = { id -> navController.navigate(Screen.DetalleModeracion.createRoute(id)) }
                    )
                }
            }
        }
    }
}

@Composable
fun StatItem(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count, fontSize = 38.sp, fontWeight = FontWeight.ExtraBold, color = Blanco)
        Text(label, fontSize = 21.sp, color = Blanco.copy(0.9f))
    }
}

@Composable
fun ListaMascotasAdmin(mascotas: List<Mascota>, onDelete: (String) -> Unit, onEdit: (String) -> Unit, onClick: (String) -> Unit, uidUsuario: String) {
    if (mascotas.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No hay mascotas en el catálogo", color = GrisSubtexto, fontSize = 21.sp) }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(mascotas) { m ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onClick(m.id) },
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Blanco)
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = m.fotoUrl, 
                        contentDescription = null, 
                        modifier = Modifier
                            .size(110.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.TopCenter
                    )
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text(m.nombre, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        Text("${m.raza} • ${m.edad}", fontSize = 18.sp, color = GrisSubtexto)
                        val isDisponible = m.estado.equals("Disponible", ignoreCase = true)
                        Text(if(isDisponible) "✓ Disponible" else "• Adoptado", color = if(isDisponible) VerdeDisponible else GrisSubtexto, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        
                        if (m.publicadoPor != uidUsuario) {
                            Text("De: Particular", fontSize = 14.sp, color = NaranjaHuellaFeliz, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (m.publicadoPor == uidUsuario) {
                        IconButton(onClick = { onEdit(m.id) }) { Icon(Icons.Default.Edit, null, tint = NaranjaHuellaFeliz, modifier = Modifier.size(24.dp)) }
                    }
                    IconButton(onClick = { onDelete(m.id) }) { Icon(Icons.Default.Delete, null, tint = RojoError, modifier = Modifier.size(24.dp)) }
                }
            }
        }
    }
}

@Composable
fun ListaModeracionAdmin(mascotas: List<Mascota>, onClick: (String) -> Unit, onApprove: (String) -> Unit, isApproved: Boolean = false) {
    if (mascotas.isEmpty()) Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Todo al día. No hay mascotas por aprobar.", color = GrisSubtexto, fontSize = 18.sp) }
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        items(mascotas) { m ->
            Card(
                modifier = Modifier.fillMaxWidth().clickable { onClick(m.id) }, 
                shape = RoundedCornerShape(16.dp), 
                colors = CardDefaults.cardColors(containerColor = Blanco)
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = m.fotoUrl, 
                        contentDescription = null, 
                        modifier = Modifier
                            .size(70.dp)
                            .clip(RoundedCornerShape(12.dp)), 
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.TopCenter
                    )
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(m.nombre, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        Text(if(isApproved) "Publicación Aprobada" else "Subido por usuario", fontSize = 15.sp, color = GrisSubtexto)
                    }
                    if (!isApproved) {
                        Button(
                            onClick = { onApprove(m.id) }, 
                            colors = ButtonDefaults.buttonColors(containerColor = VerdeDisponible), 
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Aprobar", color = Blanco)
                        }
                    } else {
                        Icon(Icons.Default.CheckCircle, null, tint = VerdeDisponible)
                    }
                }
            }
        }
    }
}

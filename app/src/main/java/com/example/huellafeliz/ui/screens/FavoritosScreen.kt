package com.example.huellafeliz.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.huellafeliz.data.repository.MascotaRepository
import com.example.huellafeliz.data.model.Mascota
import com.example.huellafeliz.navigation.Screen
import com.example.huellafeliz.ui.theme.*
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritosScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val repo = MascotaRepository()
    val userProfile by authViewModel.usuarioLogueado.collectAsState()

    LaunchedEffect(Unit) {
        authViewModel.cargarPerfil()
    }
    
    // Obtener las mascotas cuyos IDs están en la lista de favoritos del usuario
    val todasLasMascotas by repo.getMascotas().collectAsState(initial = emptyList())
    val mascotasFav = remember(todasLasMascotas, userProfile) {
        if (userProfile == null) emptyList()
        else todasLasMascotas.filter { m -> userProfile?.favoritos?.contains(m.id) == true }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Favoritos", color = Blanco, fontWeight = FontWeight.Bold, fontSize = 26.sp) },
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
            HuellaFelizBottomBar(navController, userProfile?.rol ?: "adoptante", "favoritos")
        }
    ) { padding ->
        if (mascotasFav.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("Aún no tienes favoritos 🐾", color = GrisSubtexto)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(mascotasFav) { mascota ->
                    Card(
                        onClick = { navController.navigate(Screen.Detalle.createRoute(mascota.id)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Blanco),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = mascota.fotoUrl,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.TopCenter
                            )
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text(mascota.nombre, fontWeight = FontWeight.Bold, fontSize = 22.sp)
                                Text(mascota.raza, fontSize = 18.sp, color = GrisSubtexto)
                            }
                            IconButton(onClick = { authViewModel.toggleFavorito(mascota.id) }) {
                                Icon(Icons.Default.Favorite, null, tint = NaranjaHuellaFeliz)
                            }
                        }
                    }
                }
            }
        }
    }
}

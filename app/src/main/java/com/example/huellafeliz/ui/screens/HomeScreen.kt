package com.example.huellafeliz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.huellafeliz.data.model.Mascota
import com.example.huellafeliz.navigation.Screen
import com.example.huellafeliz.ui.theme.*

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel    : AuthViewModel = viewModel(),
    mascotaViewModel: MascotaViewModel = viewModel()
) {
    var mostrarDialogoCerrar by remember { mutableStateOf(false) }
    val mascotas by mascotaViewModel.mascotasFiltradas.collectAsState()
    val query by mascotaViewModel.searchQuery.collectAsState()
    val especieFiltro by mascotaViewModel.especieFiltro.collectAsState()
    val edadFiltro by mascotaViewModel.edadFiltro.collectAsState()
    val tamanoFiltro by mascotaViewModel.tamanoFiltro.collectAsState()
    val userProfile by viewModel.usuarioLogueado.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.cargarPerfil()
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Blanco)
                    .statusBarsPadding()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Pets, null, tint = GrisTexto, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(10.dp))
                        Text("HuellaFeliz", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = NaranjaHuellaFeliz)
                    }
                    Icon(Icons.Default.Notifications, null, tint = NaranjaHuellaFeliz, modifier = Modifier.size(32.dp))
                }
                
                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { mascotaViewModel.updateSearchQuery(it) },
                        placeholder = { Text("Buscar mascota...", color = GrisSubtexto, fontSize = 22.sp) },
                        leadingIcon = { 
                            Icon(
                                imageVector = Icons.Default.Search, 
                                contentDescription = "Buscar", 
                                tint = NaranjaHuellaFeliz, 
                                modifier = Modifier.size(28.dp) 
                            ) 
                        },
                        singleLine = true,
                        modifier = Modifier.weight(1f).height(60.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = GrisFondo,
                            unfocusedContainerColor = GrisFondo,
                            focusedBorderColor = NaranjaHuellaFeliz,
                            unfocusedBorderColor = androidx.compose.ui.graphics.Color.Transparent
                        )
                    )
                    
                    Surface(
                        onClick = { navController.navigate(Screen.Filtros.route) },
                        modifier = Modifier.size(60.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = GrisFondo
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Tune, null, tint = NaranjaHuellaFeliz, modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }
        },
        bottomBar = {
            HuellaFelizBottomBar(navController, userProfile?.rol ?: "adoptante", "home")
        },
        floatingActionButton = {
            if (userProfile != null) {
                FloatingActionButton(
                    onClick = { navController.navigate(Screen.Publicar.route) },
                    containerColor = NaranjaHuellaFeliz,
                    contentColor = Blanco
                ) {
                    Icon(Icons.Default.Add, "Publicar", modifier = Modifier.size(32.dp))
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(16.dp))
            
            Text(
                text = "Mascotas disponibles • ${mascotas.size} resultados",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = GrisTexto,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            for (i in mascotas.indices step 2) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    val m1 = mascotas[i]
                    val isFav1 = userProfile?.favoritos?.contains(m1.id) == true
                    val context = androidx.compose.ui.platform.LocalContext.current
                    TarjetaMascota(
                        mascota = m1,
                        isFavorite = isFav1,
                        onFavoriteClick = { 
                            if (userProfile != null) {
                                viewModel.toggleFavorito(m1.id)
                            } else {
                                android.widget.Toast.makeText(context, "Inicia sesión para favoritos", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        navController.navigate(Screen.Detalle.createRoute(m1.id))
                    }

                    if (i + 1 < mascotas.size) {
                        val m2 = mascotas[i + 1]
                        val isFav2 = userProfile?.favoritos?.contains(m2.id) == true
                        TarjetaMascota(
                            mascota = m2,
                            isFavorite = isFav2,
                            onFavoriteClick = { 
                                if (userProfile != null) {
                                    viewModel.toggleFavorito(m2.id)
                                } else {
                                    android.widget.Toast.makeText(context, "Inicia sesión para favoritos", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            navController.navigate(Screen.Detalle.createRoute(m2.id))
                        }
                    } else {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun TarjetaMascota(
    mascota: Mascota,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val adoptado = mascota.estado.equals("Adoptado", ignoreCase = true)
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Blanco),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .background(if (mascota.fotoUrl.isEmpty()) NaranjaHuellaFeliz.copy(0.1f) else androidx.compose.ui.graphics.Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                if (mascota.fotoUrl.isNotEmpty()) {
                    AsyncImage(
                        model = mascota.fotoUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.TopCenter
                    )
                } else {
                    Icon(Icons.Default.Pets, null, tint = NaranjaHuellaFeliz, modifier = Modifier.size(40.dp))
                }
                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).padding(10.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = (if (adoptado) GrisSubtexto else VerdeDisponible).copy(alpha = 0.9f)
                ) {
                    Text(if (adoptado) "Adoptado" else "✓ Disp.", color = Blanco, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
            Column(modifier = Modifier.padding(16.dp)) {
                Text(mascota.nombre, fontWeight = FontWeight.ExtraBold, fontSize = 30.sp, color = GrisTexto)
                Text(mascota.raza, fontSize = 22.sp, color = GrisSubtexto, maxLines = 1)
                Spacer(Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CalendarToday, null, tint = GrisSubtexto, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(mascota.edad, fontSize = 21.sp, color = GrisSubtexto)
                    }
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorito",
                            tint = NaranjaHuellaFeliz,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FilterChipItem(text: String, seleccionado: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (seleccionado) NaranjaHuellaFeliz else GrisFondo,
            contentColor = if (seleccionado) Blanco else GrisSubtexto
        ),
        shape = RoundedCornerShape(20.dp),
        contentPadding = PaddingValues(horizontal = 20.dp)
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, fontSize = 22.sp)
    }
}

@Composable
fun HuellaFelizBottomBar(navController: NavController, rol: String, rutaActual: String) {
    NavigationBar(containerColor = Blanco, tonalElevation = 8.dp) {
        val esAdmin = rol == "refugio"
        
        // Item Inicio / Panel
        NavigationBarItem(
            selected = rutaActual == "home" || rutaActual == "panel_refugio",
            onClick  = { 
                val destino = if(esAdmin) Screen.PanelRefugio.route else Screen.Home.route
                navController.navigate(destino) { popUpTo(0) }
            },
            icon     = { Icon(Icons.Default.Home, null, modifier = Modifier.size(28.dp)) },
            label    = { Text("Inicio", fontSize = 18.sp) },
            colors   = navColores()
        )

        if (esAdmin) {
            // Item Publicar (Solo Admin)
            NavigationBarItem(
                selected = rutaActual == "publicar",
                onClick  = { navController.navigate(Screen.Publicar.route) },
                icon     = { Icon(Icons.Default.AddCircle, null, modifier = Modifier.size(28.dp)) },
                label    = { Text("Publicar", fontSize = 18.sp) },
                colors   = navColores()
            )
        } else {
            // Item Favoritos (Solo Adoptante)
            NavigationBarItem(
                selected = rutaActual == "favoritos",
                onClick  = { navController.navigate(Screen.Favoritos.route) },
                icon     = { Icon(Icons.Default.Favorite, null, modifier = Modifier.size(28.dp)) },
                label    = { Text("Favoritos", fontSize = 18.sp) },
                colors   = navColores()
            )
        }

        // Item Solicitudes
        NavigationBarItem(
            selected = rutaActual == "solicitudes" || rutaActual == "mis_solicitudes",
            onClick  = {
                val destino = if(esAdmin) Screen.Solicitudes.route else Screen.MisSolicitudes.route
                navController.navigate(destino)
            },
            icon     = { Icon(if(esAdmin) Icons.Default.Assignment else Icons.Default.Description, null, modifier = Modifier.size(28.dp)) },
            label    = { Text("Solicitudes", fontSize = 18.sp) },
            colors   = navColores()
        )

        // Item Perfil
        NavigationBarItem(
            selected = rutaActual == "perfil",
            onClick  = { navController.navigate(Screen.Perfil.route) },
            icon     = { Icon(Icons.Default.Person, null, modifier = Modifier.size(28.dp)) },
            label    = { Text("Perfil", fontSize = 18.sp) },
            colors   = navColores()
        )
    }
}

@Composable
private fun navColores() = NavigationBarItemDefaults.colors(
    selectedIconColor = NaranjaHuellaFeliz,
    selectedTextColor = NaranjaHuellaFeliz,
    indicatorColor    = NaranjaHuellaFeliz.copy(alpha = 0.1f),
    unselectedIconColor = GrisSubtexto,
    unselectedTextColor = GrisSubtexto
)

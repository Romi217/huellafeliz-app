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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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

@Composable
fun DetalleMascotaScreen(
    navController: NavController,
    mascotaId: String,
    authViewModel: AuthViewModel = viewModel()
) {
    val repo = MascotaRepository()
    val solRepo = SolicitudRepository()
    
    val mascota by repo.getMascotaByIdFlow(mascotaId).collectAsState(initial = null)

    var mostrarConfirmacionEliminar by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val userProfile by authViewModel.usuarioLogueado.collectAsState()

    LaunchedEffect(mascotaId) {
        if (mascotaId.isBlank()) {
            navController.popBackStack()
            return@LaunchedEffect
        }
        // La carga inicial y validación de existencia se hace vía Flow
        authViewModel.cargarPerfil()
    }

    // Validación de existencia con retraso para dar tiempo a Firebase
    LaunchedEffect(mascota) {
        if (mascota == null) {
            // Esperar un poco antes de cerrar, por si es lentitud de red
            kotlinx.coroutines.delay(3000)
            if (mascota == null) {
                Toast.makeText(context, "No se encontró información de la mascota", Toast.LENGTH_LONG).show()
                navController.popBackStack()
            }
        }
    }

    if (mostrarConfirmacionEliminar) {
        AlertDialog(
            onDismissRequest = { mostrarConfirmacionEliminar = false },
            title = { Text("Eliminar Mascota", fontWeight = FontWeight.Bold) },
            text = { Text("¿Estás seguro de que quieres eliminar esta mascota? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            val res = repo.eliminarMascota(mascotaId)
                            if (res.isSuccess) {
                                Toast.makeText(context, "Mascota eliminada", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        }
                        mostrarConfirmacionEliminar = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RojoError)
                ) {
                    Text("Eliminar", color = Blanco)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarConfirmacionEliminar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Box(modifier = Modifier.fillMaxSize().background(Blanco)) {
        mascota?.let { m ->
            val colorTema = if (userProfile?.rol == "refugio") VerdeDisponible else NaranjaHuellaFeliz

            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                // ── Cabecera con Imagen (Figma) ───────────────────────────
                Box(modifier = Modifier.fillMaxWidth().height(400.dp)) {
                    AsyncImage(
                        model = m.fotoUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.TopCenter // Para ver mejor la cabeza del animal
                    )
                    // Botones con Padding para la barra de estado
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        // Botón Volver
                        Surface(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.size(44.dp),
                            shape = CircleShape,
                            color = Blanco.copy(alpha = 0.9f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = GrisTexto, modifier = Modifier.size(20.dp))
                            }
                        }
                        // Botón Favorito
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (m.publicadoPor == userProfile?.uid) {
                                Surface(
                                    onClick = { navController.navigate(Screen.Publicar.createRoute(m.id)) },
                                    modifier = Modifier.size(44.dp),
                                    shape = CircleShape,
                                    color = Blanco.copy(alpha = 0.9f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Edit, null, tint = NaranjaHuellaFeliz, modifier = Modifier.size(24.dp))
                                    }
                                }
                                Spacer(Modifier.width(8.dp))
                                Surface(
                                    onClick = { mostrarConfirmacionEliminar = true },
                                    modifier = Modifier.size(44.dp),
                                    shape = CircleShape,
                                    color = Blanco.copy(alpha = 0.9f)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.Delete, null, tint = RojoError, modifier = Modifier.size(24.dp))
                                    }
                                }
                                Spacer(Modifier.width(8.dp))
                            }
                            
                            Surface(
                                onClick = { 
                                    if (userProfile != null) {
                                        authViewModel.toggleFavorito(m.id)
                                    } else {
                                        Toast.makeText(context, "Inicia sesión para guardar favoritos", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.size(44.dp),
                                shape = CircleShape,
                                color = Blanco.copy(alpha = 0.9f)
                            ) {
                                val esFav = userProfile?.favoritos?.contains(m.id) == true
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        if (esFav) Icons.Default.Favorite else Icons.Default.FavoriteBorder, 
                                        null, 
                                        tint = colorTema, 
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // ── Contenido (Card con bordes redondeados arriba) ───────
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = (-40).dp)
                        .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                        .background(Blanco)
                        .padding(horizontal = 24.dp, vertical = 32.dp)
                ) {
                    Surface(
                        color = VerdeDisponible.copy(0.15f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        val isDisponible = m.estado.equals("Disponible", ignoreCase = true)
                        Text(
                            if (isDisponible) "✓ Disponible" else "• Adoptado", 
                            color = if (isDisponible) VerdeDisponible else GrisSubtexto,
                            fontSize = 18.sp, 
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(m.nombre, fontSize = 42.sp, fontWeight = FontWeight.ExtraBold, color = GrisTexto)
                    Text("${m.especie} • ${m.raza}", fontSize = 24.sp, color = GrisSubtexto)

                    Spacer(Modifier.height(24.dp))

                    // Fila de Info con Iconos (Figma)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        InfoCardItem(Icons.Default.CalendarMonth, m.edad, Modifier.weight(1f))
                        InfoCardItem(Icons.Default.Straighten, m.tamaño, Modifier.weight(1f))
                    }
                    
                    Spacer(Modifier.height(16.dp))
                    
                    // Estado de Salud
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (m.vacunado) HealthBadge("Vacunado")
                        if (m.castrado) HealthBadge("Castrado")
                        if (m.desparasitado) HealthBadge("Desparasitado")
                    }

                    Spacer(Modifier.height(32.dp))

                    Text("Sobre ${m.nombre}", fontWeight = FontWeight.Bold, fontSize = 26.sp, color = GrisTexto)
                    Text(
                        text = m.descripcion, 
                        fontSize = 22.sp, 
                        color = GrisSubtexto, 
                        lineHeight = 30.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    if (m.caracter.isNotBlank()) {
                        Spacer(Modifier.height(24.dp))
                        Text("Carácter", fontWeight = FontWeight.Bold, fontSize = 26.sp, color = GrisTexto)
                        Text(
                            text = m.caracter,
                            fontSize = 22.sp,
                            color = GrisSubtexto,
                            lineHeight = 30.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    if (m.requisitos.isNotBlank()) {
                        Spacer(Modifier.height(24.dp))
                        Text("Requisitos de Adopción", fontWeight = FontWeight.Bold, fontSize = 26.sp, color = GrisTexto)
                        Text(
                            text = m.requisitos,
                            fontSize = 22.sp,
                            color = GrisSubtexto,
                            lineHeight = 30.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    Spacer(Modifier.height(32.dp))

                    // Audio Reproductor
                    m.audioUrl?.let { url ->
                        Text("Audio descriptivo 🎙️", fontWeight = FontWeight.Bold, fontSize = 26.sp, color = GrisTexto)
                        Spacer(Modifier.height(12.dp))
                        AudioPlayer(url)
                        Spacer(Modifier.height(32.dp))
                    }

                    // Contacto
                    Text("Contacto del refugio", fontWeight = FontWeight.Bold, fontSize = 26.sp, color = GrisTexto)
                    Spacer(Modifier.height(12.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = GrisFondo),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(40.dp).background(colorTema.copy(0.2f), CircleShape))
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text("Refugio Huella Feliz", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Phone, null, tint = GrisSubtexto, modifier = Modifier.size(14.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text("+591 70000000", fontSize = 19.sp, color = GrisSubtexto)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(40.dp))

                    if (m.estado.equals("Disponible", ignoreCase = true)) {
                        Button(
                            onClick = {
                                if (userProfile != null) {
                                    scope.launch {
                                        val solicitud = Solicitud(
                                            idMascota = m.id,
                                            idAdoptante = userProfile!!.uid,
                                            idRefugio = m.publicadoPor,
                                            nombreMascota = m.nombre,
                                            fotoMascota = m.fotoUrl,
                                            nombreAdoptante = userProfile!!.nombre,
                                            razaMascota = m.raza,
                                            tipoPublicador = m.tipoPublicador,
                                            nombrePublicador = if(m.tipoPublicador == "refugio") "Refugio HuellaFeliz" else "Particular"
                                        )
                                        val res = solRepo.enviarSolicitud(solicitud)
                                        if (res.isSuccess) Toast.makeText(context, "¡Solicitud enviada!", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Inicia sesión para solicitar", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colorTema)
                        ) {
                            Text("Solicitar Adopción 🐾", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                        }
                    } else {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = GrisFondo,
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                "Esta mascota ya ha sido adoptada y tiene un nuevo hogar. ❤️",
                                modifier = Modifier.padding(20.dp),
                                textAlign = TextAlign.Center,
                                fontSize = 18.sp,
                                color = GrisSubtexto,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }
?: Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = NaranjaHuellaFeliz)
        }
    }
}

@Composable
fun AudioPlayer(url: String) {
    val context = LocalContext.current
    val mediaPlayer = remember { android.media.MediaPlayer() }
    var isPlaying by remember { mutableStateOf(false) }
    var isPrepared by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableStateOf(0f) }
    var duration by remember { mutableLongStateOf(1L) }

    LaunchedEffect(isPlaying) {
        if (isPlaying) {
            while (isPlaying) {
                currentPosition = mediaPlayer.currentPosition.toFloat() / duration
                kotlinx.coroutines.delay(500)
            }
        }
    }
    
    DisposableEffect(url) {
        try {
            mediaPlayer.apply {
                reset()
                setDataSource(url)
                prepareAsync()
                setOnPreparedListener { 
                    isPrepared = true
                    duration = it.duration.toLong().coerceAtLeast(1L)
                }
                setOnCompletionListener { 
                    isPlaying = false 
                    currentPosition = 0f
                    seekTo(0)
                }
                setOnErrorListener { _, _, _ ->
                    isPrepared = false
                    isPlaying = false
                    true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        
        onDispose {
            mediaPlayer.release()
        }
    }

    Surface(
        color = GrisFondo,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    if (!isPrepared) {
                        Toast.makeText(context, "Cargando audio...", Toast.LENGTH_SHORT).show()
                        return@IconButton
                    }
                    if (isPlaying) {
                        mediaPlayer.pause()
                        isPlaying = false
                    } else {
                        mediaPlayer.start()
                        isPlaying = true
                    }
                },
                modifier = Modifier.size(56.dp).background(NaranjaHuellaFeliz, CircleShape)
            ) {
                Icon(
                    if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow, 
                    null, 
                    tint = Blanco, 
                    modifier = Modifier.size(36.dp)
                )
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(
                    text = if (isPlaying) "Reproduciendo..." else if (isPrepared) "Listo para escuchar" else "Cargando audio...", 
                    fontSize = 20.sp, 
                    fontWeight = FontWeight.SemiBold, 
                    color = GrisTexto
                )
                LinearProgressIndicator(
                    progress = { currentPosition },
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    color = NaranjaHuellaFeliz,
                    trackColor = NaranjaHuellaFeliz.copy(alpha = 0.2f)
                )
            }
        }
    }
}

@Composable
fun HealthBadge(text: String) {
    Surface(
        color = NaranjaHuellaFeliz.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = text,
            color = NaranjaHuellaFeliz,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun InfoCardItem(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        color = GrisFondo,
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, null, tint = GrisSubtexto, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = GrisTexto)
        }
    }
}

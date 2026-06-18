package com.example.huellafeliz.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.example.huellafeliz.navigation.Screen
import com.example.huellafeliz.data.network.CloudinaryService
import com.example.huellafeliz.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicarMascotaScreen(
    navController: NavController,
    mascotaId: String? = null,
    viewModel: MascotaViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    var nombre by remember { mutableStateOf("") }
    var especie by remember { mutableStateOf("Perro") }
    var raza by remember { mutableStateOf("") }
    var edad by remember { mutableStateOf("Cachorro") }
    var tamano by remember { mutableStateOf("Mediano") }
    var descripcion by remember { mutableStateOf("") }
    var caracter by remember { mutableStateOf("") }
    var requisitos by remember { mutableStateOf("") }
    var vacunado by remember { mutableStateOf(false) }
    var castrado by remember { mutableStateOf(false) }
    var desparasitado by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var audioUri by remember { mutableStateOf<Uri?>(null) }
    var existingFotoUrl by remember { mutableStateOf("") }
    var existingAudioUrl by remember { mutableStateOf<String?>(null) }
    var estadoActual by remember { mutableStateOf("Disponible") }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val uploadState by viewModel.uploadState.collectAsState()
    val userProfile by authViewModel.usuarioLogueado.collectAsState()

    val repoMascotas = remember { com.example.huellafeliz.data.repository.MascotaRepository() }

    LaunchedEffect(mascotaId) {
        if (mascotaId != null) {
            val m = repoMascotas.getMascotaById(mascotaId)
            if (m != null) {
                nombre = m.nombre
                especie = m.especie
                raza = m.raza
                edad = m.edad
                tamano = m.tamaño
                descripcion = m.descripcion
                caracter = m.caracter
                requisitos = m.requisitos
                vacunado = m.vacunado
                castrado = m.castrado
                desparasitado = m.desparasitado
                existingFotoUrl = m.fotoUrl
                existingAudioUrl = m.audioUrl
                estadoActual = m.estado
            }
        }
    }

    LaunchedEffect(uploadState) {
        if (uploadState is AuthUiState.Success) {
            Toast.makeText(context, "¡Publicado con éxito!", Toast.LENGTH_SHORT).show()
            viewModel.resetUploadState()
            val destino = if (userProfile?.rol == "refugio") Screen.PanelRefugio.route else Screen.Home.route
            navController.navigate(destino) {
                popUpTo(Screen.Home.route)
            }
        }
    }

    val launcherImagen = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri = it }
    val launcherAudio = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { audioUri = it }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (mascotaId != null) "Editar Mascota" else "Publicar Mascota", color = Blanco, fontWeight = FontWeight.Bold, fontSize = 26.sp) },
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (mascotaId != null) "Editar Información" else "Datos de la Mascota",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = GrisTexto
            )

            // Imagen selector
            Card(
                onClick = { launcherImagen.launch("image/*") },
                modifier = Modifier.fillMaxWidth().height(220.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GrisFondo),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri, 
                            contentDescription = null, 
                            modifier = Modifier.fillMaxSize(), 
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    } else if (existingFotoUrl.isNotEmpty()) {
                        AsyncImage(
                            model = existingFotoUrl, 
                            contentDescription = null, 
                            modifier = Modifier.fillMaxSize(), 
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.AddPhotoAlternate, null, modifier = Modifier.size(60.dp), tint = GrisSubtexto)
                            Text("Toca para subir foto", color = GrisTexto, fontSize = 22.sp)
                        }
                    }
                }
            }

            // Audio selector
            OutlinedButton(
                onClick = { launcherAudio.launch("audio/*") },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(24.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, NaranjaHuellaFeliz),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = NaranjaHuellaFeliz)
            ) {
                Icon(Icons.Default.Mic, null, tint = NaranjaHuellaFeliz)
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (audioUri == null) "🎙️ Subir audio descriptivo" else "🎵 Audio seleccionado",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            
            // Especie selector
            Text("Especie", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = GrisTexto)
            val especies = listOf("Perro", "Gato", "Otro")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                especies.forEach { e ->
                    FilterChip(
                        selected = especie == e,
                        onClick = { especie = e },
                        label = { Text(e, fontSize = 18.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NaranjaHuellaFeliz,
                            selectedLabelColor = Blanco
                        )
                    )
                }
            }

            OutlinedTextField(
                value = raza, 
                onValueChange = { raza = it }, 
                label = { Text("Raza") }, 
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = NaranjaHuellaFeliz, focusedLabelColor = NaranjaHuellaFeliz)
            )
            
            // Edad selector
            Text("Edad", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = GrisTexto)
            val edades = listOf("Cachorro", "Adulto", "Mayor")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                edades.forEach { e ->
                    FilterChip(
                        selected = edad == e, 
                        onClick = { edad = e }, 
                        label = { Text(e, fontSize = 18.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NaranjaHuellaFeliz,
                            selectedLabelColor = Blanco
                        )
                    )
                }
            }

            // Tamaño selector
            Text("Tamaño", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = GrisTexto)
            val tamanos = listOf("Pequeño", "Mediano", "Grande")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tamanos.forEach { t ->
                    FilterChip(
                        selected = tamano == t, 
                        onClick = { tamano = t }, 
                        label = { Text(t, fontSize = 18.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = NaranjaHuellaFeliz,
                            selectedLabelColor = Blanco
                        )
                    )
                }
            }

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 5
            )

            OutlinedTextField(
                value = caracter,
                onValueChange = { caracter = it },
                label = { Text("Carácter de la mascota") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                placeholder = { Text("Ej: Juguetón, tranquilo, sociable...") }
            )

            OutlinedTextField(
                value = requisitos,
                onValueChange = { requisitos = it },
                label = { Text("Requisitos de adopción") },
                modifier = Modifier.fillMaxWidth().height(100.dp),
                placeholder = { Text("Ej: Espacio amplio, sin otras mascotas...") }
            )

            // Estado de Salud
            Text("Estado de Salud", fontWeight = FontWeight.Bold, fontSize = 22.sp, color = GrisTexto)
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Vacunado", color = GrisTexto, fontSize = 19.sp)
                Switch(checked = vacunado, onCheckedChange = { vacunado = it }, colors = switchColores())
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Castrado", color = GrisTexto, fontSize = 19.sp)
                Switch(checked = castrado, onCheckedChange = { castrado = it }, colors = switchColores())
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Desparasitado", color = GrisTexto, fontSize = 19.sp)
                Switch(checked = desparasitado, onCheckedChange = { desparasitado = it }, colors = switchColores())
            }

            Button(
                onClick = {
                    if (uploadState is AuthUiState.Loading) return@Button
                    scope.launch {
                        var finalFotoUrl = existingFotoUrl
                        var finalAudioUrl = existingAudioUrl

                        if (imageUri != null) {
                            val uploaded = CloudinaryService.subirArchivo(imageUri!!)
                            if (uploaded != null) finalFotoUrl = uploaded
                        }

                        if (audioUri != null) {
                            val uploadedAudio = CloudinaryService.subirArchivo(audioUri!!, esAudio = true)
                            if (uploadedAudio != null) finalAudioUrl = uploadedAudio
                        }

                        if (finalFotoUrl.isNotEmpty()) {
                            if (mascotaId != null) {
                                // Lógica de actualización
                                val mascotaActualizada = com.example.huellafeliz.data.model.Mascota(
                                    id = mascotaId,
                                    nombre = nombre, especie = especie, raza = raza, 
                                    edad = edad, tamaño = tamano, descripcion = descripcion,
                                    caracter = caracter, requisitos = requisitos,
                                    fotoUrl = finalFotoUrl, audioUrl = finalAudioUrl,
                                    estado = estadoActual,
                                    publicadoPor = userProfile?.uid ?: "",
                                    tipoPublicador = userProfile?.rol ?: "particular",
                                    aprobada = userProfile?.rol == "refugio",
                                    vacunado = vacunado,
                                    castrado = castrado,
                                    desparasitado = desparasitado
                                )
                                val res = repoMascotas.publicarMascota(mascotaActualizada)
                                if (res.isSuccess) {
                                    Toast.makeText(context, "¡Actualizado!", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                            } else {
                                viewModel.publicarMascota(
                                    nombre, especie, raza, edad, tamano, 
                                    descripcion, caracter, requisitos, finalFotoUrl, finalAudioUrl, userProfile?.rol ?: "particular",
                                    vacunado, castrado, desparasitado
                                )
                            }
                        } else {
                            Toast.makeText(context, "Debes seleccionar una foto", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NaranjaHuellaFeliz),
                enabled = uploadState !is AuthUiState.Loading && nombre.isNotBlank()
            ) {
                if (uploadState is AuthUiState.Loading) {
                    CircularProgressIndicator(color = Blanco, modifier = Modifier.size(24.dp))
                } else {
                    Text(if (mascotaId != null) "Guardar Cambios" else "Publicar", fontWeight = FontWeight.Bold, fontSize = 22.sp)
                }
            }
        }
    }
}

@Composable
private fun switchColores() = SwitchDefaults.colors(
    checkedThumbColor = Blanco,
    checkedTrackColor = NaranjaHuellaFeliz,
    uncheckedThumbColor = Blanco,
    uncheckedTrackColor = GrisSubtexto.copy(alpha = 0.5f)
)

package com.example.huellafeliz.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.huellafeliz.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltrosScreen(
    navController: NavController,
    viewModel: MascotaViewModel = viewModel()
) {
    val especieFiltro by viewModel.especieFiltro.collectAsState()
    val edadFiltro by viewModel.edadFiltro.collectAsState()
    val tamanoFiltro by viewModel.tamanoFiltro.collectAsState()
    val vacunadoFiltro by viewModel.vacunadoFiltro.collectAsState()
    val castradoFiltro by viewModel.castradoFiltro.collectAsState()
    val desparasitadoFiltro by viewModel.desparasitadoFiltro.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Filtrar Mascotas", fontWeight = FontWeight.Bold, fontSize = 24.sp) },
                navigationIcon = {
                    TextButton(onClick = { navController.popBackStack() }) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = NaranjaHuellaFeliz)
                            Text(" Volver", color = NaranjaHuellaFeliz, fontSize = 20.sp)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Blanco)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Sección Especie
            FilterSectionTitle("Especie")
            val especies = listOf("Todos", "Perro", "Gato", "Otro")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                especies.forEach { chip ->
                    FilterChip(
                        selected = especieFiltro == chip,
                        onClick = { viewModel.updateEspecieFiltro(chip) },
                        label = { Text(chip, fontSize = 18.sp) },
                        colors = filterChipColors()
                    )
                }
            }

            // Sección Edad
            FilterSectionTitle("Edad")
            val edades = listOf("Todos", "Cachorro", "Adulto", "Mayor")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                edades.forEach { chip ->
                    FilterChip(
                        selected = edadFiltro == chip,
                        onClick = { viewModel.updateEdadFiltro(chip) },
                        label = { Text(chip, fontSize = 18.sp) },
                        colors = filterChipColors()
                    )
                }
            }

            // Sección Tamaño
            FilterSectionTitle("Tamaño")
            val tamanos = listOf("Todos", "Pequeño", "Mediano", "Grande")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tamanos.forEach { chip ->
                    FilterChip(
                        selected = tamanoFiltro == chip,
                        onClick = { viewModel.updateTamanoFiltro(chip) },
                        label = { Text(chip, fontSize = 18.sp) },
                        colors = filterChipColors()
                    )
                }
            }

            // Sección Estado de Salud
            FilterSectionTitle("Estado de salud")
            Card(
                colors = CardDefaults.cardColors(containerColor = Blanco),
                elevation = CardDefaults.cardElevation(0.dp),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.background(Blanco)
            ) {
                Column {
                    HealthSwitchItem("Vacunado", vacunadoFiltro) { viewModel.updateVacunadoFiltro(it) }
                    Divider(color = GrisFondo)
                    HealthSwitchItem("Castrado", castradoFiltro) { viewModel.updateCastradoFiltro(it) }
                    Divider(color = GrisFondo)
                    HealthSwitchItem("Desparasitado", desparasitadoFiltro) { viewModel.updateDesparasitadoFiltro(it) }
                }
            }

            Spacer(Modifier.weight(1f))

            // Botones Finales
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NaranjaHuellaFeliz)
            ) {
                Text("Aplicar Filtros", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = { viewModel.limpiarFiltros() },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, GrisSubtexto),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GrisTexto)
            ) {
                Text("Limpiar filtros", fontSize = 22.sp)
            }
        }
    }
}

@Composable
fun FilterSectionTitle(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        color = GrisTexto,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun HealthSwitchItem(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 20.sp, color = GrisTexto)
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Blanco,
                checkedTrackColor = NaranjaHuellaFeliz,
                uncheckedThumbColor = Blanco,
                uncheckedTrackColor = GrisSubtexto.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
fun filterChipColors() = FilterChipDefaults.filterChipColors(
    selectedContainerColor = NaranjaHuellaFeliz,
    selectedLabelColor = Blanco,
    containerColor = GrisFondo,
    labelColor = GrisTexto
)

package com.example.huellafeliz.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.huellafeliz.navigation.Screen
import com.example.huellafeliz.ui.theme.*

@Composable
fun RegistroScreen(
    navController: NavController,
    viewModel    : AuthViewModel = viewModel()
) {
    var nombre     by remember { mutableStateOf("") }
    var apellido   by remember { mutableStateOf("") }
    var correo     by remember { mutableStateOf("") }
    var telefono   by remember { mutableStateOf("") }
    var ciudad     by remember { mutableStateOf("") }
    var direccion  by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var verClave   by remember { mutableStateOf(false) }
    var rol        by remember { mutableStateOf("adoptante") }

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            Toast.makeText(context, "¡Cuenta creada con éxito!", Toast.LENGTH_SHORT).show()
            viewModel.resetState()
            navController.navigate(Screen.Home.route) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header ────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Brush.verticalGradient(listOf(NaranjaHuellaFeliz, NaranjaOscuro))
                )
        ) {
            IconButton(
                onClick  = { navController.popBackStack() },
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Blanco)
            }
            Text(
                text       = "Crear Cuenta",
                fontSize   = 26.sp,
                fontWeight = FontWeight.Bold,
                color      = Blanco,
                modifier   = Modifier.align(Alignment.Center)
            )
        }

        // ── Formulario ────────────────────────────────────────────────────
        Card(
            modifier  = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .offset(y = (-20).dp),
            shape     = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors    = CardDefaults.cardColors(containerColor = Blanco)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Selección de Rol
                val roles = listOf("adoptante", "refugio")
                TabRow(
                    selectedTabIndex = roles.indexOf(rol),
                    containerColor   = Blanco,
                    contentColor     = NaranjaHuellaFeliz,
                    divider          = {}
                ) {
                    roles.forEach { r ->
                        Tab(
                            selected = rol == r,
                            onClick  = { rol = r },
                            text     = {
                                Text(
                                    text       = if (r == "adoptante") "Soy Adoptante" else "Soy Refugio",
                                    fontWeight = if (rol == r) FontWeight.Bold else FontWeight.Normal,
                                    fontSize   = 17.sp
                                )
                            }
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                // Nombre
                OutlinedTextField(
                    value         = nombre,
                    onValueChange = { if (it.all { char -> char.isLetter() || char.isWhitespace() }) nombre = it },
                    label         = { Text(if (rol == "adoptante") "Nombre" else "Nombre del Refugio") },
                    leadingIcon   = { Icon(Icons.Default.Person, null, tint = NaranjaHuellaFeliz) },
                    placeholder   = { Text(if (rol == "adoptante") "Ej: Ana" else "Ej: Patitas Felices") },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    colors        = campoColores()
                )

                // Apellido (Solo para adoptantes)
                if (rol == "adoptante") {
                    OutlinedTextField(
                        value         = apellido,
                        onValueChange = { if (it.all { char -> char.isLetter() || char.isWhitespace() }) apellido = it },
                        label         = { Text("Apellido") },
                        leadingIcon   = { Icon(Icons.Default.Person, null, tint = NaranjaHuellaFeliz) },
                        placeholder   = { Text("Ej: García") },
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(12.dp),
                        colors        = campoColores()
                    )
                }


                // Correo
                OutlinedTextField(
                    value           = correo,
                    onValueChange   = { correo = it },
                    label           = { Text("Correo electrónico") },
                    leadingIcon     = { Icon(Icons.Default.Email, null, tint = NaranjaHuellaFeliz) },
                    placeholder     = { Text("ejemplo@correo.com") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine      = true,
                    modifier        = Modifier.fillMaxWidth(),
                    shape           = RoundedCornerShape(12.dp),
                    colors          = campoColores()
                )

                // Teléfono
                OutlinedTextField(
                    value           = telefono,
                    onValueChange   = { telefono = it },
                    label           = { Text("Teléfono") },
                    leadingIcon     = { Icon(Icons.Default.Phone, null, tint = NaranjaHuellaFeliz) },
                    placeholder     = { Text("+591 70000000") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine      = true,
                    modifier        = Modifier.fillMaxWidth(),
                    shape           = RoundedCornerShape(12.dp),
                    colors          = campoColores()
                )

                // Ciudad
                OutlinedTextField(
                    value         = ciudad,
                    onValueChange = { ciudad = it },
                    label         = { Text("Ciudad") },
                    leadingIcon   = { Icon(Icons.Default.LocationOn, null, tint = NaranjaHuellaFeliz) },
                    placeholder   = { Text("Cochabamba") },
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    colors        = campoColores()
                )

                // Dirección (Solo para refugios)
                if (rol == "refugio") {
                    OutlinedTextField(
                        value         = direccion,
                        onValueChange = { direccion = it },
                        label         = { Text("Dirección") },
                        leadingIcon   = { Icon(Icons.Default.Home, null, tint = NaranjaHuellaFeliz) },
                        placeholder   = { Text("Calle Av. Juan de la Rosa #123") },
                        singleLine    = true,
                        modifier      = Modifier.fillMaxWidth(),
                        shape         = RoundedCornerShape(12.dp),
                        colors        = campoColores()
                    )
                }

                // Contraseña
                OutlinedTextField(
                    value                = contrasena,
                    onValueChange        = { contrasena = it },
                    label                = { Text("Contraseña") },
                    leadingIcon          = { Icon(Icons.Default.Lock, null, tint = NaranjaHuellaFeliz) },
                    placeholder          = { Text("Mínimo 6 caracteres") },
                    visualTransformation = if (verClave) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon         = {
                        IconButton(onClick = { verClave = !verClave }) {
                            Icon(
                                if (verClave) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                null, tint = GrisSubtexto
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine      = true,
                    modifier        = Modifier.fillMaxWidth(),
                    shape           = RoundedCornerShape(12.dp),
                    colors          = campoColores()
                )

                // Error
                if (uiState is AuthUiState.Error) {
                    Text(
                        text      = (uiState as AuthUiState.Error).mensaje,
                        color     = RojoError,
                        fontSize  = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier  = Modifier.fillMaxWidth()
                    )
                }

                // Botón crear cuenta
                Button(
                    onClick  = {
                        // Validación simple antes de enviar
                        if (telefono.length < 7) {
                            Toast.makeText(context, "Teléfono inválido", Toast.LENGTH_SHORT).show()
                        } else {
                            viewModel.registrar(
                                nombre.trim(), apellido.trim(), correo.trim(),
                                telefono.trim(), ciudad.trim(), direccion.trim(), contrasena, rol
                            )
                        }
                    },
                    enabled  = nombre.isNotBlank() 
                               && (rol == "refugio" || apellido.isNotBlank())
                               && (rol == "adoptante" || direccion.isNotBlank())
                               && correo.isNotBlank() && contrasena.length >= 6
                               && uiState !is AuthUiState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = NaranjaHuellaFeliz)
                ) {
                    if (uiState is AuthUiState.Loading) {
                        CircularProgressIndicator(color = Blanco, modifier = Modifier.size(22.dp))
                    } else {
                        Text("Crear Cuenta", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    }
                }

                // Ya tengo cuenta
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("¿Ya tienes cuenta? ", color = GrisSubtexto, fontSize = 21.sp)
                    Text(
                        text       = "Inicia sesión",
                        color      = NaranjaHuellaFeliz,
                        fontSize   = 21.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier   = Modifier.clickable { navController.popBackStack() }
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun campoColores() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor  = NaranjaHuellaFeliz,
    focusedLabelColor   = NaranjaHuellaFeliz,
    cursorColor         = NaranjaHuellaFeliz
)

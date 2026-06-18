package com.example.huellafeliz.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.huellafeliz.R
import com.example.huellafeliz.navigation.Screen
import com.example.huellafeliz.ui.theme.*

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel    : AuthViewModel = viewModel()
) {
    var correo     by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var verClave   by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()
    val userProfile by viewModel.usuarioLogueado.collectAsState()

    // Navegar según el rol cuando el login sea exitoso
    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Success) {
            val destino = if (userProfile?.rol == "refugio") Screen.PanelRefugio.route else Screen.Home.route
            viewModel.resetState()
            navController.navigate(destino) {
                popUpTo(Screen.Login.route) { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header naranja (igual al Figma) ──────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    Brush.verticalGradient(listOf(NaranjaHuellaFeliz, NaranjaOscuro))
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.imagen),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text       = "¡Bienvenido!",
                    fontSize   = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Blanco
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text     = "Inicia sesión en HuellaFeliz",
                    fontSize = 22.sp,
                    color    = Blanco.copy(alpha = 0.85f)
                )
            }
        }

        // ── Tarjeta blanca con el formulario ─────────────────────────────
        Card(
            modifier  = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .offset(y = (-28).dp),
            shape     = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(8.dp),
            colors    = CardDefaults.cardColors(containerColor = Blanco)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Campo correo
                OutlinedTextField(
                    value         = correo,
                    onValueChange = { correo = it },
                    label         = { Text("Correo electrónico") },
                    leadingIcon   = { Icon(Icons.Default.Email, null, tint = NaranjaHuellaFeliz) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine    = true,
                    modifier      = Modifier.fillMaxWidth(),
                    shape         = RoundedCornerShape(12.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor   = NaranjaHuellaFeliz,
                        focusedLabelColor    = NaranjaHuellaFeliz,
                        cursorColor          = NaranjaHuellaFeliz
                    )
                )

                // Campo contraseña
                OutlinedTextField(
                    value         = contrasena,
                    onValueChange = { contrasena = it },
                    label         = { Text("Contraseña") },
                    leadingIcon   = { Icon(Icons.Default.Lock, null, tint = NaranjaHuellaFeliz) },
                    trailingIcon  = {
                        IconButton(onClick = { verClave = !verClave }) {
                            Icon(
                                if (verClave) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null,
                                tint = GrisSubtexto
                            )
                        }
                    },
                    visualTransformation = if (verClave) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions      = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine           = true,
                    modifier             = Modifier.fillMaxWidth(),
                    shape                = RoundedCornerShape(12.dp),
                    colors               = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor  = NaranjaHuellaFeliz,
                        focusedLabelColor   = NaranjaHuellaFeliz,
                        cursorColor         = NaranjaHuellaFeliz
                    )
                )

                // ¿Olvidaste tu contraseña?
                Text(
                    text      = "¿Olvidaste tu contraseña?",
                    color     = NaranjaHuellaFeliz,
                    fontSize  = 16.sp,
                    modifier  = Modifier.align(Alignment.End)
                )

                // Mensaje de error
                if (uiState is AuthUiState.Error) {
                    Text(
                        text      = (uiState as AuthUiState.Error).mensaje,
                        color     = RojoError,
                        fontSize  = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier  = Modifier.fillMaxWidth()
                    )
                }

                // Botón iniciar sesión
                Button(
                    onClick  = { viewModel.login(correo.trim(), contrasena) },
                    enabled  = correo.isNotBlank() && contrasena.isNotBlank()
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
                        Text("Iniciar Sesión", fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    }
                }

                // Ir a registro
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("¿No tienes cuenta? ", color = GrisSubtexto, fontSize = 21.sp)
                    Text(
                        text     = "Regístrate",
                        color    = NaranjaHuellaFeliz,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable {
                            navController.navigate(Screen.Registro.route)
                        }
                    )
                }
            }
        }
    }
}

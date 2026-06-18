package com.example.huellafeliz.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huellafeliz.data.repository.AuthRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthUiState {
    object Idle    : AuthUiState()
    object Loading : AuthUiState()
    object Success : AuthUiState()
    data class Error(val mensaje: String) : AuthUiState()
}

class AuthViewModel : ViewModel() {

    private val repo = AuthRepository()

    init {
        cargarPerfil()
    }

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    val hayUsuarioActivo: Boolean
        get() = repo.usuarioActual != null

    fun login(correo: String, contrasena: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = repo.login(correo, contrasena)
            if (result.isSuccess) {
                val uid = result.getOrNull()?.uid ?: ""
                val perfilRes = repo.getPerfil(uid)
                if (perfilRes.isSuccess) {
                    _usuarioLogueado.value = perfilRes.getOrNull()
                }
                _uiState.value = AuthUiState.Success
            } else {
                val exception = result.exceptionOrNull()
                val mensaje = when {
                    exception?.message?.contains("credential") == true || 
                    exception?.message?.contains("password") == true -> "Correo o contraseña incorrectos"
                    exception?.message?.contains("user-not-found") == true -> "El usuario no existe"
                    else -> "Error al iniciar sesión. Verifica tu conexión."
                }
                _uiState.value = AuthUiState.Error(mensaje)
            }
        }
    }

    fun registrar(nombre: String, apellido: String, correo: String, telefono: String, ciudad: String, direccion: String, contrasena: String, rol: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = repo.registrar(nombre, apellido, correo, telefono, ciudad, direccion, contrasena, rol)
            _uiState.value = if (result.isSuccess) AuthUiState.Success
                             else AuthUiState.Error(result.exceptionOrNull()?.localizedMessage ?: "Error al registrarse")
        }
    }

    fun cerrarSesion() {
        repo.cerrarSesion()
        _uiState.value = AuthUiState.Idle
    }

    fun resetState() { _uiState.value = AuthUiState.Idle }

    // Obtener datos del usuario logueado
    private val _usuarioLogueado = MutableStateFlow<com.example.huellafeliz.data.model.Usuario?>(null)
    val usuarioLogueado = _usuarioLogueado.asStateFlow()

    fun cargarPerfil() {
        viewModelScope.launch {
            val uid = repo.usuarioActual?.uid ?: return@launch
            val result = repo.getPerfil(uid)
            if (result.isSuccess) {
                _usuarioLogueado.value = result.getOrNull()
            }
        }
    }

    private val firestore = com.google.firebase.firestore.FirebaseFirestore.getInstance()

    fun toggleFavorito(mascotaId: String) {
        val user = _usuarioLogueado.value ?: return
        val nuevosFavoritos = user.favoritos.toMutableList()
        
        if (nuevosFavoritos.contains(mascotaId)) {
            nuevosFavoritos.remove(mascotaId)
        } else {
            nuevosFavoritos.add(mascotaId)
        }

        val updatedUser = user.copy(favoritos = nuevosFavoritos)
        _usuarioLogueado.value = updatedUser

        viewModelScope.launch {
            try {
                firestore.collection("usuarios").document(user.uid)
                    .update("favoritos", nuevosFavoritos).await()
            } catch (e: Exception) {
                // Si falla en Firebase, revertimos localmente
                _usuarioLogueado.value = user
            }
        }
    }
}

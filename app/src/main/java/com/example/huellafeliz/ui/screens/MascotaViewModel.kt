package com.example.huellafeliz.ui.screens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.huellafeliz.data.model.Mascota
import com.example.huellafeliz.data.repository.MascotaRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MascotaViewModel : ViewModel() {
    private val repo = MascotaRepository()
    private val auth = FirebaseAuth.getInstance()

    // Estados de búsqueda y filtros
    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _especieFiltro = MutableStateFlow("Todos")
    val especieFiltro = _especieFiltro.asStateFlow()

    private val _edadFiltro = MutableStateFlow("Todos")
    val edadFiltro = _edadFiltro.asStateFlow()

    private val _tamanoFiltro = MutableStateFlow("Todos")
    val tamanoFiltro = _tamanoFiltro.asStateFlow()

    private val _vacunadoFiltro = MutableStateFlow(false)
    val vacunadoFiltro = _vacunadoFiltro.asStateFlow()

    private val _castradoFiltro = MutableStateFlow(false)
    val castradoFiltro = _castradoFiltro.asStateFlow()

    private val _desparasitadoFiltro = MutableStateFlow(false)
    val desparasitadoFiltro = _desparasitadoFiltro.asStateFlow()

    // Lista de mascotas original de Firestore
    private val _mascotas = repo.getMascotas()
        .catch { emit(emptyList()) }
        .stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )

    // Lista filtrada reactiva usando combine con array para manejar múltiples flows
    val mascotasFiltradas = combine(
        _mascotas, _searchQuery, _especieFiltro, _edadFiltro, _tamanoFiltro,
        _vacunadoFiltro, _castradoFiltro, _desparasitadoFiltro
    ) { flows ->
        val lista = flows[0] as List<Mascota>
        val query = flows[1] as String
        val especie = flows[2] as String
        val edad = flows[3] as String
        val tamano = flows[4] as String
        val vac = flows[5] as Boolean
        val cast = flows[6] as Boolean
        val desp = flows[7] as Boolean

        lista.filter { mascota ->
            val esDisponible = mascota.estado.equals("Disponible", ignoreCase = true)
            val esAprobada = mascota.aprobada || mascota.tipoPublicador == "refugio" || mascota.tipoPublicador == ""
            val coincideBusqueda = mascota.nombre.contains(query, ignoreCase = true) || 
                                   mascota.raza.contains(query, ignoreCase = true)
            val coincideEspecie = especie == "Todos" || mascota.especie.equals(especie, ignoreCase = true)
            val coincideEdad = edad == "Todos" || mascota.edad.equals(edad, ignoreCase = true)
            val coincideTamano = tamano == "Todos" || mascota.tamaño.equals(tamano, ignoreCase = true)
            
            val coincideVac = !vac || mascota.vacunado
            val coincideCast = !cast || mascota.castrado
            val coincideDesp = !desp || mascota.desparasitado
            
            esDisponible && esAprobada && coincideBusqueda && coincideEspecie && coincideEdad && coincideTamano &&
            coincideVac && coincideCast && coincideDesp
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun updateSearchQuery(query: String) { _searchQuery.value = query }
    fun updateEspecieFiltro(especie: String) { _especieFiltro.value = especie }
    fun updateEdadFiltro(edad: String) { _edadFiltro.value = edad }
    fun updateTamanoFiltro(tamano: String) { _tamanoFiltro.value = tamano }
    
    fun updateVacunadoFiltro(value: Boolean) { _vacunadoFiltro.value = value }
    fun updateCastradoFiltro(value: Boolean) { _castradoFiltro.value = value }
    fun updateDesparasitadoFiltro(value: Boolean) { _desparasitadoFiltro.value = value }

    fun limpiarFiltros() {
        _especieFiltro.value = "Todos"
        _edadFiltro.value = "Todos"
        _tamanoFiltro.value = "Todos"
        _vacunadoFiltro.value = false
        _castradoFiltro.value = false
        _desparasitadoFiltro.value = false
        _searchQuery.value = ""
    }

    // Publicación
    private val _uploadState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uploadState = _uploadState.asStateFlow()

    fun publicarMascota(
        nombre: String, especie: String, raza: String, edad: String,
        tamaño: String, descripcion: String, caracter: String, requisitos: String, 
        fotoUrl: String, audioUrl: String?, rolUsuario: String,
        vacunado: Boolean = false, castrado: Boolean = false, desparasitado: Boolean = false
    ) {
        viewModelScope.launch {
            _uploadState.value = AuthUiState.Loading
            val mascota = Mascota(
                nombre         = nombre,
                especie        = especie,
                raza           = raza,
                edad           = edad,
                tamaño         = tamaño,
                descripcion    = descripcion,
                caracter       = caracter,
                requisitos     = requisitos,
                fotoUrl        = fotoUrl,
                audioUrl       = audioUrl,
                publicadoPor   = auth.currentUser?.uid ?: "",
                tipoPublicador = rolUsuario,
                aprobada       = rolUsuario == "refugio",
                vacunado       = vacunado,
                castrado       = castrado,
                desparasitado  = desparasitado
            )
            val result = repo.publicarMascota(mascota)
            _uploadState.value = if (result.isSuccess) AuthUiState.Success 
                                 else AuthUiState.Error(result.exceptionOrNull()?.message ?: "Error al publicar")
        }
    }
    
    fun resetUploadState() { _uploadState.value = AuthUiState.Idle }
}

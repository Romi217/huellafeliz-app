package com.example.huellafeliz.data.model

data class Mascota(
    val id            : String = "",
    val nombre        : String = "",
    val especie       : String = "",
    val raza          : String = "",
    val edad          : String = "", // Cachorro, Adulto, Mayor
    val tamaño        : String = "", // Pequeño, Mediano, Grande
    val descripcion   : String = "",
    val caracter      : String = "",
    val requisitos    : String = "",
    val fotoUrl       : String = "",
    val audioUrl      : String? = null,
    val estado        : String = "Disponible", // Disponible, Adoptado
    val publicadoPor  : String = "", // UID del usuario
    val tipoPublicador: String = "", // particular, refugio
    val aprobada      : Boolean = false, // Solo para particulares, requiere aprobación
    val vacunado      : Boolean = false,
    val castrado      : Boolean = false,
    val desparasitado : Boolean = false,
    val fecha         : Long   = System.currentTimeMillis()
)

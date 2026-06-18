package com.example.huellafeliz.data.model

data class Solicitud(
    val id         : String = "",
    val idMascota  : String = "",
    val idAdoptante: String = "",
    val idRefugio  : String = "", // UID del publicador (refugio o particular)
    val estado     : String = "pendiente", // pendiente, aceptado, rechazado
    val fecha      : Long   = System.currentTimeMillis(),
    // Para mostrar datos rápidos en la lista sin hacer más queries
    val nombreMascota: String = "",
    val fotoMascota  : String = "",
    val nombreAdoptante: String = "",
    val razaMascota    : String = "",
    val nombrePublicador: String = "",
    val tipoPublicador : String = "" // refugio o particular
)

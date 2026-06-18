package com.example.huellafeliz.data.model

data class Usuario(
    val uid      : String = "",
    val nombre   : String = "",
    val apellido : String? = null,
    val correo   : String = "",
    val telefono : String = "",
    val ciudad   : String = "",
    val direccion: String? = null,
    val favoritos: List<String> = emptyList(),
    val rol      : String = "adoptante"
)

package com.example.huellafeliz.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.example.huellafeliz.data.model.Usuario
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth      = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    val usuarioActual: FirebaseUser?
        get() = auth.currentUser

    // ── Registro ────────────────────────────────────────────────────────────
    suspend fun registrar(
        nombre    : String,
        apellido  : String,
        correo    : String,
        telefono  : String,
        ciudad    : String,
        direccion : String,
        contrasena: String,
        rol       : String
    ): Result<FirebaseUser> {
        return try {
            val resultado = auth
                .createUserWithEmailAndPassword(correo, contrasena)
                .await()

            val user = resultado.user!!

            // Crear un mapa con los datos básicos
            val datosUsuario = mutableMapOf(
                "uid"    to user.uid,
                "nombre" to nombre,
                "correo" to correo,
                "telefono" to telefono,
                "ciudad" to ciudad,
                "rol"    to rol
            )

            // Solo agregar apellido si no está vacío (para adoptantes)
            if (apellido.isNotBlank()) {
                datosUsuario["apellido"] = apellido
            }

            // Solo agregar dirección si no está vacía (para refugios)
            if (direccion.isNotBlank()) {
                datosUsuario["direccion"] = direccion
            }

            firestore
                .collection("usuarios")
                .document(user.uid)
                .set(datosUsuario)
                .await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Login ───────────────────────────────────────────────────────────────
    suspend fun login(correo: String, contrasena: String): Result<FirebaseUser> {
        return try {
            val resultado = auth
                .signInWithEmailAndPassword(correo, contrasena)
                .await()
            Result.success(resultado.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // ── Cerrar sesión ────────────────────────────────────────────────────────
    fun cerrarSesion() = auth.signOut()

    // ── Obtener perfil ──────────────────────────────────────────────────────
    suspend fun getPerfil(uid: String): Result<Usuario> {
        return try {
            val snapshot = firestore.collection("usuarios").document(uid).get().await()
            val usuario = snapshot.toObject(Usuario::class.java)
            if (usuario != null) Result.success(usuario)
            else Result.failure(Exception("Usuario no encontrado"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

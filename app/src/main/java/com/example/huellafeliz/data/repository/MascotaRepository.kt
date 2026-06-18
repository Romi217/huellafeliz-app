package com.example.huellafeliz.data.repository

import com.example.huellafeliz.data.model.Mascota
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MascotaRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("mascotas")

    // Obtener todas las mascotas en tiempo real
    fun getMascotas(): Flow<List<Mascota>> = callbackFlow {
        val subscription = collection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val lista = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Mascota::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                // Ordenar localmente por fecha si existe, si no por ID
                val listaOrdenada = lista.sortedByDescending { it.fecha }
                trySend(listaOrdenada)
            }
        awaitClose { subscription.remove() }
    }

    // Obtener una mascota por ID en tiempo real
    fun getMascotaByIdFlow(id: String): Flow<Mascota?> = callbackFlow {
        val subscription = collection.document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val mascota = snapshot?.toObject(Mascota::class.java)?.copy(id = snapshot.id)
                trySend(mascota)
            }
        awaitClose { subscription.remove() }
    }

    // Obtener una mascota por ID
    suspend fun getMascotaById(id: String): Mascota? {
        return try {
            val doc = collection.document(id).get().await()
            doc.toObject(Mascota::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    // Publicar o actualizar mascota
    suspend fun publicarMascota(mascota: Mascota): Result<String> {
        return try {
            val docRef = if (mascota.id.isEmpty()) collection.document() else collection.document(mascota.id)
            val mascotaConId = if (mascota.id.isEmpty()) mascota.copy(id = docRef.id) else mascota
            docRef.set(mascotaConId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Actualizar estado de mascota (Ej: de Disponible a Adoptado)
    suspend fun actualizarEstado(id: String, nuevoEstado: String): Result<Unit> {
        return try {
            collection.document(id).update("estado", nuevoEstado).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Eliminar mascota
    suspend fun eliminarMascota(id: String): Result<Unit> {
        return try {
            collection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener mascotas de un publicador específico
    fun getMascotasByPublicador(uid: String): Flow<List<Mascota>> = callbackFlow {
        val subscription = collection
            .whereEqualTo("publicadoPor", uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val lista = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Mascota::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(lista)
            }
        awaitClose { subscription.remove() }
    }
}

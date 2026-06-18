package com.example.huellafeliz.data.repository

import com.example.huellafeliz.data.model.Solicitud
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class SolicitudRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("solicitudes")

    suspend fun enviarSolicitud(solicitud: Solicitud): Result<Unit> {
        return try {
            val docRef = collection.document()
            collection.document(docRef.id).set(solicitud.copy(id = docRef.id)).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getSolicitudesParaRefugio(uidRefugio: String): Flow<List<Solicitud>> = callbackFlow {
        // Si el usuario es el refugio principal (admin), quizás quieras ver TODO.
        // Por ahora, filtramos por idRefugio, pero asegúrate de que este ID sea el correcto.
        val subscription = collection
            .whereEqualTo("idRefugio", uidRefugio)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val lista = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Solicitud::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(lista)
            }
        awaitClose { subscription.remove() }
    }

    // Nueva función para que el Admin vea absolutamente todas las solicitudes de adopción del sistema
    fun getAllSolicitudes(): Flow<List<Solicitud>> = callbackFlow {
        val subscription = collection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val lista = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Solicitud::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(lista)
            }
        awaitClose { subscription.remove() }
    }

    fun getMisSolicitudes(uidAdoptante: String): Flow<List<Solicitud>> = callbackFlow {
        val subscription = collection
            .whereEqualTo("idAdoptante", uidAdoptante)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val lista = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Solicitud::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(lista)
            }
        awaitClose { subscription.remove() }
    }

    fun getSolicitudByIdFlow(id: String): Flow<Solicitud?> = callbackFlow {
        val subscription = collection.document(id)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val sol = snapshot?.toObject(Solicitud::class.java)?.copy(id = snapshot.id)
                trySend(sol)
            }
        awaitClose { subscription.remove() }
    }

    suspend fun getSolicitudById(id: String): Solicitud? {
        return try {
            val doc = collection.document(id).get().await()
            doc.toObject(Solicitud::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun actualizarEstadoSolicitud(idSolicitud: String, nuevoEstado: String): Result<Unit> {
        return try {
            collection.document(idSolicitud).update("estado", nuevoEstado).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

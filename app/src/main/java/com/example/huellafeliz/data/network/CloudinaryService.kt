package com.example.huellafeliz.data.network

import android.content.Context
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object CloudinaryService {

    private var inicializado = false

    fun init(context: Context) {
        if (!inicializado) {
            val config = mapOf(
                "cloud_name" to "dmaohtuus", // Reemplazar con el tuyo
                "api_key"    to "227367172598148",
                "api_secret" to "y71dp8gpeoA2A-0dsZ5wSU1_MRo", // Esto no se suele poner en cliente, mejor usar unsigned upload
                "secure"     to true
            )
            MediaManager.init(context, config)
            inicializado = true
        }
    }

    suspend fun subirArchivo(uri: Uri, esAudio: Boolean = false): String? = suspendCancellableCoroutine { continuation ->
        val request = MediaManager.get().upload(uri)
            .unsigned("huellafeliz_preset")
            
        if (esAudio) {
            request.option("resource_type", "video") // El audio en Cloudinary se sube como tipo 'video'
        }

        request.callback(object : UploadCallback {
            override fun onStart(requestId: String) {}
            override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
            override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                continuation.resume(resultData["secure_url"] as String)
            }
            override fun onError(requestId: String, error: ErrorInfo) {
                continuation.resume(null)
            }
            override fun onReschedule(requestId: String, error: ErrorInfo) {}
        }).dispatch()
    }
}

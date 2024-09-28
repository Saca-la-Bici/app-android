package com.kotlin.sacalabici.data.network

import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class MultipartManager {

    fun prepareFilePart(partName: String, fileUri: Uri): MultipartBody.Part? {
        val file = File(fileUri.path!!) // Asegúrate de manejar correctamente la URI
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull()) // Cambia esto
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    fun uriToFile(context: Context, uri: Uri): File? {
        val file = File(context.cacheDir, "tempFile") // Cambia "tempFile" a un nombre adecuado
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            return file
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}


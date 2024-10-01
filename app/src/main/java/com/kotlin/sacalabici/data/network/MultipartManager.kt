package com.kotlin.sacalabici.data.network

import android.content.Context
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

class MultipartManager {

    fun prepareFilePart(partName: String, fileUri: Uri): MultipartBody.Part? {
        val file = File(fileUri.path!!)
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(partName, file.name, requestFile)
    }

    fun uriToFile(context: Context, uri: Uri): File {
        val file = File(context.cacheDir, "tempFile" + getExtension(uri, context))
        context.contentResolver.openInputStream(uri).use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream?.copyTo(outputStream)
            }
        }
        return file
    }

    private fun getExtension(uri: Uri, context: Context): String {
        val fileExtension: String? = context.contentResolver.getType(uri)
        return when (fileExtension) {
            "image/jpeg" -> ".jpg"
            "image/png" -> ".png"
            "image/gif" -> ".gif"
            else -> ".tmp"
        }
    }


}


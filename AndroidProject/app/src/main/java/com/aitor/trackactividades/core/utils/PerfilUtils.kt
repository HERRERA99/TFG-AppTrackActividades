package com.aitor.trackactividades.core.utils

import android.content.Context
import android.net.Uri
import java.io.File

fun uriStringToFile(context: Context, uriString: Uri): File {
    val uri = uriString
    val inputStream = context.contentResolver.openInputStream(uri)!!
    val tempFile = File.createTempFile("image", ".jpg", context.cacheDir)
    tempFile.outputStream().use { output ->
        inputStream.copyTo(output)
    }
    return tempFile
}
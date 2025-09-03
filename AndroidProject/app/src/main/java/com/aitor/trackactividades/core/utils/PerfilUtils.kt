package com.aitor.trackactividades.core.utils

import android.content.Context
import android.net.Uri
import java.io.File

/**
 * Convierte un objeto [Uri] en un archivo temporal almacenado en la caché de la aplicación.
 *
 * Este método se utiliza comúnmente para obtener un archivo a partir de un recurso referenciado por un `Uri`,
 * como una imagen seleccionada desde la galería, para poder manejarlo como un archivo local.
 *
 * @param context El contexto de la aplicación, necesario para acceder al ContentResolver y la caché.
 * @param uriString El [Uri] del recurso que se desea convertir en archivo.
 * @return Un [File] temporal creado en el directorio de caché (`context.cacheDir`) con el contenido del recurso.
 *
 * @throws FileNotFoundException si el URI no puede resolverse.
 * @throws IOException si ocurre un error durante la lectura o escritura del archivo.
 *
 * Ejemplo de uso:
 * ```
 * val file = uriStringToFile(context, uri)
 * ```
 */
fun uriStringToFile(context: Context, uriString: Uri): File {
    val uri = uriString
    val inputStream = context.contentResolver.openInputStream(uri)!!
    val tempFile = File.createTempFile("image", ".jpg", context.cacheDir)
    tempFile.outputStream().use { output ->
        inputStream.copyTo(output)
    }
    return tempFile
}
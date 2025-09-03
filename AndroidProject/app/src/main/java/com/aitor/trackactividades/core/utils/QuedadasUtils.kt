package com.aitor.trackactividades.core.utils

/**
 * Extrae el pueblo, la provincia y el país desde una dirección textual,
 * eliminando los números y retornando solo los tres últimos componentes
 * significativos (sin dígitos).
 *
 * Se espera que la dirección esté separada por comas, y que los últimos tres elementos
 * representen (en orden) el país, la provincia y el pueblo.
 *
 * Ejemplo:
 * ```
 * val direccion = "Calle Falsa 123, 28080, Madrid, Comunidad de Madrid, España"
 * val resultado = extraerPuebloProvinciaPaisSinNumeros(direccion)
 * println(resultado) // "Madrid, Comunidad de Madrid, España"
 * ```
 *
 * @param direccion Cadena con la dirección completa, separada por comas.
 * @return Una cadena con los últimos tres componentes significativos (pueblo, provincia, país),
 *         o la dirección original si no hay suficientes partes.
 */
fun extraerPuebloProvinciaPaisSinNumeros(direccion: String): String {
    val partes = direccion.split(",")
        .map { it.trim().replace(Regex("\\d+"), "").trim() }
        .filter { it.any { c -> c.isLetter() } }

    if (partes.size < 3) return direccion

    val pais = partes[partes.size - 1]
    val provincia = partes[partes.size - 2]
    val pueblo = partes[partes.size - 3]

    return "$pueblo, $provincia, $pais"
}
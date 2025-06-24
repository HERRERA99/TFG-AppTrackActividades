package com.aitor.trackactividades.core.utils

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
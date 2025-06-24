package com.aitor.trackactividades.core.utils

/**
 * Valida que la contraseña sea segura.
 * Requisitos:
 * - Al menos 8 caracteres.
 * - Al menos una letra mayúscula.
 * - Al menos una letra minúscula.
 * - Al menos un número.
 */
fun isPasswordSecure(password: String): Boolean {
    val passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$"
    return password.matches(passwordRegex.toRegex())
}

/**
 * Valida que el nombre de usuario cumpla con las reglas:
 * - No contiene espacios.
 * - Solo permite letras, números, guiones bajos (_) y guiones (-).
 * - Longitud mínima de 3 caracteres y máxima de 20 (opcional).
 */
fun isUsernameValid(username: String): Boolean {
    val usernameRegex = "^[a-zA-Z0-9_-]{3,20}$"
    return username.matches(usernameRegex.toRegex())
}
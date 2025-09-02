package com.aitor.trackactividades.core.utils

import org.junit.Test
import org.junit.jupiter.api.Assertions

class ValidationUtilsTest {
    /* isPasswordSecure */

    @Test
    fun contrasenaConMayusculasMinusculasYDigitosEsValida() {
        Assertions.assertTrue(isPasswordSecure("Password123"))
    }

    @Test
    fun contrasenaMuyCortaYSinMayusculasEsInvalida() {
        Assertions.assertFalse(isPasswordSecure("pass123"))
    }

    @Test
    fun contrasenaSinMinusculasEsInvalida() {
        Assertions.assertFalse(isPasswordSecure("PASSWORD123"))
    }

    @Test
    fun contrasenaSinDigitosEsInvalida() {
        Assertions.assertFalse(isPasswordSecure("Password"))
    }

    @Test
    fun contrasenaValidaConMezclaDeCaracteres() {
        Assertions.assertTrue(isPasswordSecure("Pass1234"))
    }

    /* isUsernameValid */

    @Test
    fun nombreDeUsuarioValidoConGuionBajo() {
        Assertions.assertTrue(isUsernameValid("user_name"))
    }

    @Test
    fun nombreDeUsuarioValidoConGuionMedio() {
        Assertions.assertTrue(isUsernameValid("user-name"))
    }

    @Test
    fun nombreDeUsuarioConEspacioEsInvalido() {
        Assertions.assertFalse(isUsernameValid("user name"))
    }

    @Test
    fun nombreDeUsuarioDemasiadoCortoEsInvalido() {
        Assertions.assertFalse(isUsernameValid("us"))
    }

    @Test
    fun nombreDeUsuarioDemasiadoLargoEsInvalido() {
        Assertions.assertFalse(isUsernameValid("asdasdadadasdasdadasad"))
    }

    @Test
    fun nombreDeUsuarioConCaracterInvalidoEsInvalido() {
        Assertions.assertFalse(isUsernameValid("user@name"))
    }

    @Test
    fun nombreDeUsuarioAlfanumericoValido() {
        Assertions.assertTrue(isUsernameValid("user123"))
    }

}
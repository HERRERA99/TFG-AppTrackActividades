package com.aitor.trackactividades.core.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class QuedadasUtilsTest {
    @Test
    fun direccionCompleta() {
        val input = "Calle Falsa 123, 28080, Madrid, Comunidad de Madrid, España"
        val esperado = "Madrid, Comunidad de Madrid, España"
        val resultado = extraerPuebloProvinciaPaisSinNumeros(input)
        assertEquals(esperado, resultado)
    }

    @Test
    fun direccionSinNumeros() {
        val input = "Madrid, Comunidad de Madrid, España"
        val esperado = "Madrid, Comunidad de Madrid, España"
        val resultado = extraerPuebloProvinciaPaisSinNumeros(input)
        assertEquals(esperado, resultado)
    }

    @Test
    fun direccionConMuchoRuidoNumerico() {
        val input = "1234, 5678, Almería 04001, Andalucía 234, España 5"
        val esperado = "Almería, Andalucía, España"
        val resultado = extraerPuebloProvinciaPaisSinNumeros(input)
        assertEquals(esperado, resultado)
    }

    @Test
    fun direccionConMenosDeTresElementos() {
        val input = "Barcelona, España"
        val esperado = "Barcelona, España"
        val resultado = extraerPuebloProvinciaPaisSinNumeros(input)
        assertEquals(esperado, resultado)
    }
}
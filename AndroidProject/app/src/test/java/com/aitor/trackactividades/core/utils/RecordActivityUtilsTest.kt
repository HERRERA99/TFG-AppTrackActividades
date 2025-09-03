package com.aitor.trackactividades.core.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import com.aitor.trackactividades.core.model.Modalidades


class RecordActivityUtilsTest {
    /* nombreAutomatico */

    @Test
    fun testManana() {
        val hora = LocalDateTime.of(2025, 6, 25, 8, 0)
        val resultado = nombreAutomatico(hora, Modalidades.CICLISMO_CARRETERA)
        assertEquals("Ciclismo de Carretera por la mañana", resultado)
    }

    @Test
    fun testTarde() {
        val hora = LocalDateTime.of(2025, 6, 25, 15, 0)
        val resultado = nombreAutomatico(hora, Modalidades.CAMINATA)
        assertEquals("Caminata por la tarde", resultado)
    }

    @Test
    fun testNoche() {
        val hora = LocalDateTime.of(2025, 6, 25, 20, 0)
        val resultado = nombreAutomatico(hora, Modalidades.CORRER)
        assertEquals("Correr por la noche", resultado)
    }

    @Test
    fun testAmanecer() {
        val hora = LocalDateTime.of(2025, 6, 25, 4, 30)
        val resultado = nombreAutomatico(hora, Modalidades.CICLISMO_MONTAÑA)
        assertEquals("Ciclismo de Montaña al amanecer", resultado)
    }

    /* calcularDesnivelPositivo */

    @Test
    fun soloAscensos() {
        val altitudes = listOf(100.0, 120.0, 140.0, 160.0)
        assertEquals(60.0, calcularDesnivelPositivo(altitudes))
    }

    @Test
    fun ascensosYDescensos() {
        val altitudes = listOf(100.0, 150.0, 130.0, 180.0)
        assertEquals(100.0, calcularDesnivelPositivo(altitudes))
    }

    @Test
    fun soloDescensos() {
        val altitudes = listOf(300.0, 200.0, 100.0)
        assertEquals(0.0, calcularDesnivelPositivo(altitudes))
    }

    @Test
    fun altitudesEstables() {
        val altitudes = listOf(100.0, 100.0, 100.0)
        assertEquals(0.0, calcularDesnivelPositivo(altitudes))
    }

    @Test
    fun listaVaciaOUnElemento() {
        assertEquals(0.0, calcularDesnivelPositivo(emptyList()))
        assertEquals(0.0, calcularDesnivelPositivo(listOf(100.0)))
    }
}
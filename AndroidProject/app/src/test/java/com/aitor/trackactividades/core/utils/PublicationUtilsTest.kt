package com.aitor.trackactividades.core.utils

import com.aitor.trackactividades.core.utils.PublicationUtils.tiempoTranscurrido
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class PublicationUtilsTest {
    private val ahora = LocalDateTime.of(2025, 6, 25, 12, 0)

    @Test
    fun menosDeUnMinutoTest() {
        val fecha = ahora.minusSeconds(30)
        assertEquals("justo ahora", tiempoTranscurrido(fecha, ahora))
    }

    @Test
    fun haceAlgunosMinutosTest() {
        val fecha = ahora.minusMinutes(10)
        assertEquals("hace 10 minuto(s)", tiempoTranscurrido(fecha, ahora))
    }

    @Test
    fun haceAlgunasHorasTest() {
        val fecha = ahora.minusHours(5)
        assertEquals("hace 5 hora(s)", tiempoTranscurrido(fecha, ahora))
    }

    @Test
    fun ayerTest() {
        val fecha = ahora.minusDays(1)
        assertEquals("ayer", tiempoTranscurrido(fecha, ahora))
    }

    @Test
    fun haceAlgunosDiasTest() {
        val fecha = ahora.minusDays(3)
        assertEquals("hace 3 día(s)", tiempoTranscurrido(fecha, ahora))
    }

    @Test
    fun haceAlgunasSemanasTest() {
        val fecha = ahora.minusDays(14)
        assertEquals("hace 2 semana(s)", tiempoTranscurrido(fecha, ahora))
    }

    @Test
    fun haceAlgunosMesesTest() {
        val fecha = ahora.minusDays(90)
        assertEquals("hace 3 mes(es)", tiempoTranscurrido(fecha, ahora))
    }

    @Test
    fun haceAlgunosAñosTest() {
        val fecha = ahora.minusDays(800)
        assertEquals("hace 2 año(s)", tiempoTranscurrido(fecha, ahora))
    }
}
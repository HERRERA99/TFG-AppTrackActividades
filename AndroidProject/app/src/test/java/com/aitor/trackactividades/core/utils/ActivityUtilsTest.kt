package com.aitor.trackactividades.core.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ActivityUtilsTest {
    @Test
    fun testLessThanOneMinute() {
        assertEquals("59s", formatSeconds(59.0))
        assertEquals("0s", formatSeconds(0.0))
        assertEquals("1s", formatSeconds(1.0))
    }

    @Test
    fun testExactlyOneMinute() {
        assertEquals("1min", formatSeconds(60.0))
    }

    @Test
    fun testMinutesAndSeconds() {
        assertEquals("2min 5s", formatSeconds(125.0))
        assertEquals("3min", formatSeconds(180.0))
    }

    @Test
    fun testExactlyOneHour() {
        assertEquals("1h", formatSeconds(3600.0))
    }

    @Test
    fun testHoursMinutesAndSeconds() {
        assertEquals("1h 1min 1s", formatSeconds(3661.0))
        assertEquals("2h 30min 0s", formatSeconds(9000.0))
        assertEquals("2h 0min 1s", formatSeconds(7201.0))
    }

    @Test
    fun testDecimalInput() {
        assertEquals("1min 5s", formatSeconds(65.9))
        assertEquals("59s", formatSeconds(59.9))
    }
}
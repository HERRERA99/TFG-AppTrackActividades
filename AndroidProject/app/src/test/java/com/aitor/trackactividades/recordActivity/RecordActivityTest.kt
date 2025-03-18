package com.aitor.trackactividades.recordActivity

import android.location.Location
import com.aitor.trackactividades.recordActivity.presentation.RecordActivityViewModel
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class RecordActivityViewModelTest {

    private val viewModel = RecordActivityViewModel(mock())

    @Test
    fun `calculateSlope returns correct slope`() {
        // Mock locations
        val lastLocation = mock<Location>()
        val currentLocation = mock<Location>()

        // Simulación de datos
        whenever(lastLocation.altitude).thenReturn(619.0)
        whenever(currentLocation.altitude).thenReturn(688.0)
        whenever(lastLocation.distanceTo(currentLocation)).thenReturn(1000f)

        // Calcular pendiente
        val slope = viewModel.calculateSlope(lastLocation, currentLocation)

        // Comprobación: (110 - 100) / 50 * 100 = 20.0
        assertEquals(6.9f, slope, 0.01f)
    }

    @Test
    fun `calculateSlope returns zero when distance is zero`() {
        val lastLocation = mock<Location>()
        val currentLocation = mock<Location>()

        whenever(lastLocation.altitude).thenReturn(100.0)
        whenever(currentLocation.altitude).thenReturn(110.0)
        whenever(lastLocation.distanceTo(currentLocation)).thenReturn(0f)

        val slope = viewModel.calculateSlope(lastLocation, currentLocation)
        assertEquals(0f, slope, 0.01f)
    }
}

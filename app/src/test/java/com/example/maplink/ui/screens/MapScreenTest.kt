package com.example.maplink.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Test

class MapScreenTest {

    @Test
    fun defaultMapStyleUrlUsesOpenStreetMapStyle() {
        assertEquals(
            "https://demotiles.maplibre.org/style.json",
            defaultMapStyleUrl()
        )
    }

    @Test
    fun defaultMapCenterUsesAVisibleLocation() {
        assertEquals(51.5074, defaultMapCenterLatitude(), 0.0001)
        assertEquals(-0.1278, defaultMapCenterLongitude(), 0.0001)
        assertEquals(12.0, defaultMapZoom(), 0.0001)
    }
}

package com.example.maplink

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.maplink.ui.theme.MapLinkTheme
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MapLibre.getInstance(
            this,
            "",
            WellKnownTileServer.MapLibre
        )

        enableEdgeToEdge()

        setContent {
            MapLinkTheme {
                MapLinkApp()
            }
        }
    }
}
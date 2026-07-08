package com.example.maplink

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.example.maplink.service.LocationServiceManager
import com.example.maplink.ui.theme.MapLinkTheme
import org.maplibre.android.MapLibre
import org.maplibre.android.WellKnownTileServer

class MainActivity : ComponentActivity() {

    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            if (isGranted) {
                println("Notification permission granted")
            } else {
                println("Notification permission denied")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }

        LocationServiceManager.startIfAllowed(this)

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

    override fun onStart() {
        super.onStart()
        LocationServiceManager.startIfAllowed(this)
    }

    override fun onResume() {
        super.onResume()
        LocationServiceManager.startIfAllowed(this)
    }
}
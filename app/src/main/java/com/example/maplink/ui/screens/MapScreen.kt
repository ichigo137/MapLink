package com.example.maplink.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import org.maplibre.android.MapLibre
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView

fun defaultMapStyleUrl(): String = "https://demotiles.maplibre.org/style.json"
fun defaultMapZoom(): Float = 12.0f

@Composable
fun MapScreen() {
    val context = LocalContext.current
    var locationStatus by remember { mutableStateOf("Waiting for location permission…") }
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    var mapLibreMap by remember { mutableStateOf<MapLibreMap?>(null) }
    var permissionRequested by remember { mutableStateOf(false) }
    var locationPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        locationPermissionGranted = granted
        locationStatus = if (granted) {
            "Location permission granted"
        } else {
            "Location permission denied"
        }
    }

    LaunchedEffect(locationPermissionGranted) {
        if (locationPermissionGranted) {
            locationStatus = "Requesting location…"
            val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            currentLocation = location
                            locationStatus = "Location found"
                            mapLibreMap?.let { map ->
                                updateMapLocation(map, location)
                            }
                        } else {
                            locationStatus = "Location unavailable"
                        }
                    }
                    .addOnFailureListener {
                        locationStatus = "Unable to get location"
                    }
            } catch (e: Exception) {
                locationStatus = "Location error: ${e.message}"
            }
        } else if (!permissionRequested) {
            permissionRequested = true
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val currentLocationState = rememberUpdatedState(currentLocation)

    val mapView = remember(context) {
        MapLibre.getInstance(context.applicationContext)

        MapView(context).apply {
            getMapAsync { map ->
                mapLibreMap = map
                map.setStyle(defaultMapStyleUrl()) {
                    map.uiSettings.isCompassEnabled = true
                    map.uiSettings.isRotateGesturesEnabled = true
                    map.uiSettings.isAttributionEnabled = true
                    map.uiSettings.isScrollGesturesEnabled = true
                    map.uiSettings.isZoomGesturesEnabled = true

                    currentLocationState.value?.let { location ->
                        updateMapLocation(map, location)
                    }
                }
            }
        }
    }

    DisposableEffect(mapView) {
        mapView.onStart()
        mapView.onResume()

        onDispose {
            mapView.onPause()
            mapView.onStop()
            mapView.onDestroy()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )

        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = locationStatus,
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

private fun updateMapLocation(map: MapLibreMap, location: Location) {
    val center = LatLng(location.latitude, location.longitude)
    map.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 15.0))
    map.addMarker(
        MarkerOptions()
            .position(center)
            .title("You are here")
            .snippet("Current location")
    )
}

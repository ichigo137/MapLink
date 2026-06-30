package com.example.maplink.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.camera.CameraUpdateFactory

import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import android.Manifest
import com.google.accompanist.permissions.isGranted

import android.annotation.SuppressLint
import com.google.android.gms.location.LocationServices

import org.maplibre.android.annotations.MarkerOptions

@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)@Composable
fun MapScreen() {

    val locationPermission = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    LaunchedEffect(Unit) {
        if (!locationPermission.status.isGranted) {
            locationPermission.launchPermissionRequest()
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->

            MapView(context).apply {
                onCreate(null)

                getMapAsync { map ->

                    map.setStyle(
                        Style.Builder().fromUri(
                            "https://api.maptiler.com/maps/streets-v2/style.json?key=JvQZA9dPsbe7nrIqvjEZ"
                        )
                    ) {

                        val fusedLocationClient =
                            LocationServices.getFusedLocationProviderClient(context)

                        fusedLocationClient.lastLocation
                            .addOnSuccessListener { location ->

                                if (location != null) {

                                    map.moveCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                            LatLng(location.latitude, location.longitude),
                                            17.0
                                        )
                                    )

                                    map.addMarker(
                                        MarkerOptions()
                                            .position(
                                                LatLng(
                                                    location.latitude,
                                                    location.longitude
                                                )
                                            )
                                            .title("You are here 📍")
                                    )

                                } else {

                                    // Fallback to Kolkata if no location is available
                                    map.moveCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                            LatLng(22.5726, 88.3639),
                                            13.0
                                        )
                                    )

                                }

                            }

                    }

                }
            }

        }
    )
}
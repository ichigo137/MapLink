package com.example.maplink.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maplink.data.repository.LocationRepository
import com.example.maplink.ui.screens.friends.FriendsViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import org.maplibre.android.annotations.Marker
import org.maplibre.android.annotations.MarkerOptions
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import com.example.maplink.ui.components.FriendMarkerGenerator
@SuppressLint("MissingPermission")
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen() {

    val context = LocalContext.current

    val friendsViewModel: FriendsViewModel = viewModel()
    val friends by friendsViewModel.friends.collectAsState()

    val locationPermission = rememberPermissionState(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val locationRepository = remember {
        LocationRepository(context)
    }

    var mapLibreMap by remember {
        mutableStateOf<MapLibreMap?>(null)
    }

    val friendMarkers = remember {
        mutableListOf<Marker>()
    }

    LaunchedEffect(locationPermission.status) {

        if (!locationPermission.status.isGranted) {
            locationPermission.launchPermissionRequest()
        } else {
            locationRepository.startLocationUpdates()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            locationRepository.stopLocationUpdates()
        }
    }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->

            MapView(ctx).apply {

                onCreate(null)

                getMapAsync { map ->

                    mapLibreMap = map

                    map.setStyle(
                        Style.Builder().fromUri(
                            "https://api.maptiler.com/maps/streets-v2/style.json?key=JvQZA9dPsbe7nrIqvjEZ"
                        )
                    ) {

                        val fusedLocationClient =
                            LocationServices.getFusedLocationProviderClient(ctx)

                        fusedLocationClient.lastLocation
                            .addOnSuccessListener { location ->

                                if (location != null) {

                                    map.moveCamera(
                                        CameraUpdateFactory.newLatLngZoom(
                                            LatLng(
                                                location.latitude,
                                                location.longitude
                                            ),
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

    LaunchedEffect(friends, mapLibreMap) {

        val map = mapLibreMap ?: return@LaunchedEffect

        friendMarkers.forEach {
            it.remove()
        }
        friendMarkers.clear()

        friends.forEach { friend ->

            if (friend.latitude == 0.0 &&
                friend.longitude == 0.0
            ) return@forEach

            val icon = FriendMarkerGenerator.create(
                context = context,
                name = friend.name,
                online = friend.online
            )

            val marker = map.addMarker(
                MarkerOptions()
                    .position(
                        LatLng(
                            friend.latitude,
                            friend.longitude
                        )
                    )
                    .title(friend.name)
                    .snippet("@${friend.username}")
                    .icon(icon)
            )
            friendMarkers.add(marker)
        }
    }
}
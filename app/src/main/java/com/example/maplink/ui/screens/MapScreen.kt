package com.example.maplink.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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
import com.example.maplink.data.repository.Friend
import com.example.maplink.ui.components.FriendMarkerGenerator
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

    var mapLibreMap by remember {
        mutableStateOf<MapLibreMap?>(null)
    }

    val friendMarkers = remember {
        mutableListOf<Marker>()
    }

    LaunchedEffect(locationPermission.status) {

        if (!locationPermission.status.isGranted) {
            locationPermission.launchPermissionRequest()
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
                            LocationServices
                                .getFusedLocationProviderClient(ctx)

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
                                            LatLng(
                                                22.5726,
                                                88.3639
                                            ),
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

        val map = mapLibreMap
            ?: return@LaunchedEffect

        friendMarkers.forEach {
            it.remove()
        }

        friendMarkers.clear()

        friends.forEach { friend ->

            if (
                friend.latitude == 0.0 &&
                friend.longitude == 0.0
            ) {
                return@forEach
            }

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
                    .snippet(
                        "@${friend.username} • ${formatPresence(friend)}"
                    )
                    .icon(icon)
            )

            friendMarkers.add(marker)
        }
    }
}

private fun formatPresence(friend: Friend): String {

    if (friend.online) {
        return "Online"
    }

    val lastUpdatedMillis =
        friend.lastUpdated?.toDate()?.time
            ?: return "Last seen unknown"

    val elapsedMillis =
        (System.currentTimeMillis() - lastUpdatedMillis)
            .coerceAtLeast(0L)

    val seconds = elapsedMillis / 1000L
    val minutes = seconds / 60L
    val hours = minutes / 60L
    val days = hours / 24L

    return when {
        seconds < 60L ->
            "Last seen ${seconds}s ago"

        minutes < 60L ->
            "Last seen ${minutes}m ago"

        hours < 24L ->
            "Last seen ${hours}h ago"

        else ->
            "Last seen ${days}d ago"
    }
}
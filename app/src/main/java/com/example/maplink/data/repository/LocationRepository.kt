package com.example.maplink.data.repository

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.annotation.SuppressLint
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority

class LocationRepository(
    context: Context
) {

    private val firestore = FirebaseFirestore.getInstance()

    private val auth = FirebaseAuth.getInstance()

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private lateinit var locationCallback: LocationCallback

    fun updateLocation(
        latitude: Double,
        longitude: Double
    ) {

        val uid = auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(uid)
            .update(
                mapOf(
                    "latitude" to latitude,
                    "longitude" to longitude,
                    "online" to true,
                    "lastUpdated" to com.google.firebase.Timestamp.now()
                )
            )
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L
        )
            .setMinUpdateIntervalMillis(5000L)
            .build()

        locationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult) {

                val location: Location = locationResult.lastLocation ?: return

                updateLocation(
                    location.latitude,
                    location.longitude
                )
            }
        }

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}

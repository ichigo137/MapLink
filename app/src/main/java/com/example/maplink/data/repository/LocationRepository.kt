package com.example.maplink.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LocationRepository(
    context: Context
) {

    private val firestore = FirebaseFirestore.getInstance()

    private val auth = FirebaseAuth.getInstance()

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var locationCallback: LocationCallback? = null

    fun updateLocation(
        latitude: Double,
        longitude: Double
    ) {

        val uid = auth.currentUser?.uid ?: return

        val userDocument = firestore.collection("users")
            .document(uid)

        userDocument.get()
            .addOnSuccessListener { document ->

                val sharingEnabled =
                    document.getBoolean("locationSharingEnabled")
                        ?: true

                if (!sharingEnabled) {
                    return@addOnSuccessListener
                }

                userDocument.update(
                    mapOf(
                        "latitude" to latitude,
                        "longitude" to longitude,
                        "online" to true,
                        "lastUpdated" to Timestamp.now()
                    )
                )
            }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {

        if (locationCallback != null) {
            return
        }

        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            10000L
        )
            .setMinUpdateIntervalMillis(5000L)
            .build()

        val callback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult) {

                val location: Location =
                    locationResult.lastLocation ?: return

                updateLocation(
                    location.latitude,
                    location.longitude
                )
            }
        }

        locationCallback = callback

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper()
        )
    }

    fun stopLocationUpdates() {

        val callback = locationCallback ?: return

        fusedLocationClient.removeLocationUpdates(callback)

        locationCallback = null
    }
}
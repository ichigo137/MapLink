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

    private val firestore =
        FirebaseFirestore.getInstance()

    private val auth =
        FirebaseAuth.getInstance()

    private val fusedLocationClient:
            FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private var locationCallback: LocationCallback? = null

    private var lastUploadedLocation: Location? = null

    private var lastUploadTimeMillis: Long = 0L

    @Volatile
    private var sharingEnabled: Boolean = false

    fun setSharingEnabled(
        enabled: Boolean
    ) {

        sharingEnabled = enabled

        if (!enabled) {
            stopLocationUpdates()
        }
    }

    private fun uploadLocation(
        location: Location
    ) {

        if (!sharingEnabled) {
            return
        }

        val uid =
            auth.currentUser?.uid ?: return

        firestore.collection("users")
            .document(uid)
            .update(
                mapOf(
                    "latitude" to location.latitude,
                    "longitude" to location.longitude,
                    "online" to true,
                    "lastUpdated" to Timestamp.now()
                )
            )
            .addOnSuccessListener {

                lastUploadedLocation = location

                lastUploadTimeMillis =
                    System.currentTimeMillis()
            }
    }

    @SuppressLint("MissingPermission")
    fun startLocationUpdates() {

        if (!sharingEnabled) {
            return
        }

        if (locationCallback != null) {
            return
        }

        val locationRequest =
            LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                LOCATION_INTERVAL_MS
            )
                .setMinUpdateIntervalMillis(
                    MIN_LOCATION_INTERVAL_MS
                )
                .build()

        val callback =
            object : LocationCallback() {

                override fun onLocationResult(
                    locationResult: LocationResult
                ) {

                    if (!sharingEnabled) {
                        return
                    }

                    val location =
                        locationResult.lastLocation
                            ?: return

                    if (!shouldUploadLocation(location)) {
                        return
                    }

                    uploadLocation(location)
                }
            }

        locationCallback = callback

        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            callback,
            Looper.getMainLooper()
        )
    }

    private fun shouldUploadLocation(
        newLocation: Location
    ): Boolean {

        val previousLocation =
            lastUploadedLocation ?: return true

        val distanceMeters =
            previousLocation.distanceTo(newLocation)

        val elapsedMillis =
            System.currentTimeMillis() -
                    lastUploadTimeMillis

        return distanceMeters >= MIN_UPLOAD_DISTANCE_METERS ||
                elapsedMillis >= MAX_UPLOAD_SILENCE_MS
    }

    fun stopLocationUpdates() {

        val callback = locationCallback

        if (callback != null) {

            fusedLocationClient.removeLocationUpdates(callback)

            locationCallback = null
        }

        lastUploadedLocation = null

        lastUploadTimeMillis = 0L
    }

    companion object {

        private const val LOCATION_INTERVAL_MS =
            2_000L

        private const val MIN_LOCATION_INTERVAL_MS =
            5_000L

        private const val MIN_UPLOAD_DISTANCE_METERS =
            25f

        private const val MAX_UPLOAD_SILENCE_MS =
            5_000L
    }
}
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

    private var lastObservedLocation: Location? = null
    private var stationaryCallbackCount: Int = 0

    private var trackingMode =
        TrackingMode.MOVING

    @Volatile
    private var sharingEnabled = false

    private enum class TrackingMode {
        MOVING,
        STATIONARY
    }

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

        startLocationRequest()
    }

    @SuppressLint("MissingPermission")
    private fun startLocationRequest() {

        if (!sharingEnabled) {
            return
        }

        val request =
            createLocationRequest()

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

                    updateTrackingMode(location)

                    if (shouldUploadLocation(location)) {
                        uploadLocation(location)
                    }
                }
            }

        locationCallback = callback

        fusedLocationClient.requestLocationUpdates(
            request,
            callback,
            Looper.getMainLooper()
        )
    }

    private fun createLocationRequest():
            LocationRequest {

        return when (trackingMode) {

            TrackingMode.MOVING -> {

                LocationRequest.Builder(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    MOVING_INTERVAL_MS
                )
                    .setMinUpdateIntervalMillis(
                        MOVING_MIN_INTERVAL_MS
                    )
                    .build()
            }

            TrackingMode.STATIONARY -> {

                LocationRequest.Builder(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    STATIONARY_INTERVAL_MS
                )
                    .setMinUpdateIntervalMillis(
                        STATIONARY_MIN_INTERVAL_MS
                    )
                    .build()
            }
        }
    }

    private fun updateTrackingMode(
        newLocation: Location
    ) {

        val previousLocation =
            lastObservedLocation

        lastObservedLocation = newLocation

        if (previousLocation == null) {
            return
        }

        val distance =
            previousLocation.distanceTo(newLocation)

        when (trackingMode) {

            TrackingMode.MOVING -> {

                if (distance < STATIONARY_DISTANCE_METERS) {

                    stationaryCallbackCount++

                    if (
                        stationaryCallbackCount >=
                        STATIONARY_CALLBACK_THRESHOLD
                    ) {

                        stationaryCallbackCount = 0

                        switchTrackingMode(
                            TrackingMode.STATIONARY
                        )
                    }

                } else {

                    stationaryCallbackCount = 0
                }
            }

            TrackingMode.STATIONARY -> {

                if (distance >= MOVING_DISTANCE_METERS) {

                    switchTrackingMode(
                        TrackingMode.MOVING
                    )
                }
            }
        }
    }

    private fun switchTrackingMode(
        newMode: TrackingMode
    ) {

        if (trackingMode == newMode) {
            return
        }

        trackingMode = newMode

        restartLocationRequest()
    }

    @SuppressLint("MissingPermission")
    private fun restartLocationRequest() {

        val oldCallback =
            locationCallback ?: return

        fusedLocationClient
            .removeLocationUpdates(oldCallback)

        locationCallback = null

        startLocationRequest()
    }

    private fun shouldUploadLocation(
        newLocation: Location
    ): Boolean {

        val previousLocation =
            lastUploadedLocation ?: return true

        val distance =
            previousLocation.distanceTo(newLocation)

        val elapsed =
            System.currentTimeMillis() -
                    lastUploadTimeMillis

        val maxSilence =
            when (trackingMode) {

                TrackingMode.MOVING ->
                    MOVING_MAX_UPLOAD_SILENCE_MS

                TrackingMode.STATIONARY ->
                    STATIONARY_MAX_UPLOAD_SILENCE_MS
            }

        return distance >= MIN_UPLOAD_DISTANCE_METERS ||
                elapsed >= maxSilence
    }

    fun stopLocationUpdates() {

        val callback = locationCallback

        if (callback != null) {

            fusedLocationClient
                .removeLocationUpdates(callback)

            locationCallback = null
        }

        lastUploadedLocation = null
        lastObservedLocation = null

        lastUploadTimeMillis = 0L
        stationaryCallbackCount = 0

        trackingMode = TrackingMode.MOVING
    }

    companion object {

        private const val MOVING_INTERVAL_MS =
            5_000L

        private const val MOVING_MIN_INTERVAL_MS =
            5_000L

        private const val MOVING_MAX_UPLOAD_SILENCE_MS =
            5_000L


        private const val STATIONARY_INTERVAL_MS =
            15_000L

        private const val STATIONARY_MIN_INTERVAL_MS =
            10_000L

        private const val STATIONARY_MAX_UPLOAD_SILENCE_MS =
            20_000L


        private const val MIN_UPLOAD_DISTANCE_METERS =
            25f

        private const val STATIONARY_DISTANCE_METERS =
            10f

        private const val MOVING_DISTANCE_METERS =
            20f

        private const val STATIONARY_CALLBACK_THRESHOLD =
            3
    }
}
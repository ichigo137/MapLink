package com.example.maplink.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import com.example.maplink.data.repository.LocationRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class LocationTrackingService : Service() {

    private lateinit var locationRepository: LocationRepository

    override fun onCreate() {
        super.onCreate()

        locationRepository = LocationRepository(applicationContext)

        createNotificationChannel()
        startAsForegroundService()

        checkSharingAndStartTracking()
    }

    private fun checkSharingAndStartTracking() {

        val uid =
            FirebaseAuth.getInstance()
                .currentUser
                ?.uid

        if (uid == null) {

            locationRepository.setSharingEnabled(false)
            stopSelf()

            return
        }

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->

                val sharingEnabled =
                    document.getBoolean(
                        "locationSharingEnabled"
                    ) ?: true

                locationRepository
                    .setSharingEnabled(sharingEnabled)

                if (sharingEnabled) {

                    locationRepository
                        .startLocationUpdates()

                } else {

                    stopSelf()
                }
            }
            .addOnFailureListener {

                locationRepository
                    .setSharingEnabled(false)

                stopSelf()
            }
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        return START_STICKY
    }

    override fun onDestroy() {

        locationRepository.setSharingEnabled(false)

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun startAsForegroundService() {

        val notification = createNotification()

        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notification,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            } else {
                0
            }
        )
    }

    private fun createNotification(): Notification {

        return NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )
            .setContentTitle("MapLink")
            .setContentText("Sharing your location with friends")
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Location Sharing",
                NotificationManager.IMPORTANCE_LOW
            )

            channel.description =
                "Shows when MapLink is sharing your location"

            val notificationManager =
                getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID = "maplink_location_channel"
        private const val NOTIFICATION_ID = 1001
    }
}
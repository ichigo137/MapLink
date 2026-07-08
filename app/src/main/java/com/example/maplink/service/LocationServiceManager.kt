package com.example.maplink.service

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth

object LocationServiceManager {

    fun startIfAllowed(context: Context) {

        val fineLocationGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseLocationGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val userLoggedIn =
            FirebaseAuth.getInstance().currentUser != null

        if (!userLoggedIn) {
            return
        }

        if (!fineLocationGranted && !coarseLocationGranted) {
            return
        }

        val intent =
            Intent(
                context,
                LocationTrackingService::class.java
            )

        ContextCompat.startForegroundService(
            context,
            intent
        )
    }

    fun stop(context: Context) {

        val intent =
            Intent(
                context,
                LocationTrackingService::class.java
            )

        context.stopService(intent)
    }
}
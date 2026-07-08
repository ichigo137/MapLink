package com.example.maplink.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

enum class LocationPermissionState {
    FOREGROUND_NOT_GRANTED,
    BACKGROUND_NOT_GRANTED,
    BACKGROUND_GRANTED
}

object LocationPermissionManager {

    fun hasForegroundLocationPermission(
        context: Context
    ): Boolean {

        val fineGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        return fineGranted || coarseGranted
    }

    fun hasBackgroundLocationPermission(
        context: Context
    ): Boolean {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return hasForegroundLocationPermission(context)
        }

        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getPermissionState(
        context: Context
    ): LocationPermissionState {

        if (!hasForegroundLocationPermission(context)) {
            return LocationPermissionState.FOREGROUND_NOT_GRANTED
        }

        if (!hasBackgroundLocationPermission(context)) {
            return LocationPermissionState.BACKGROUND_NOT_GRANTED
        }

        return LocationPermissionState.BACKGROUND_GRANTED
    }
}
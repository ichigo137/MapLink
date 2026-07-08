package com.example.maplink.ui.screens.profile

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

enum class LocationPermissionIssue {
    NONE,
    FOREGROUND_DENIED,
    FOREGROUND_BLOCKED,
    BACKGROUND_MISSING
}

@Composable
fun LocationPermissionHandler(
    onPermissionsReady: () -> Unit,
    content: @Composable (
        requestEnableLocationSharing: () -> Unit,
        permissionIssue: LocationPermissionIssue
    ) -> Unit
) {

    val context = LocalContext.current
    val activity = context.findActivity()
    val lifecycleOwner = LocalLifecycleOwner.current

    var permissionIssue by remember {
        mutableStateOf(LocationPermissionIssue.NONE)
    }

    var showBackgroundExplanation by remember {
        mutableStateOf(false)
    }

    var showBlockedExplanation by remember {
        mutableStateOf(false)
    }

    var waitingForSettingsResult by remember {
        mutableStateOf(false)
    }

    fun hasForegroundPermission(): Boolean {

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

    fun hasBackgroundPermission(): Boolean {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            return hasForegroundPermission()
        }

        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun continueAfterForegroundPermission() {

        if (!hasForegroundPermission()) {
            return
        }

        if (hasBackgroundPermission()) {

            permissionIssue = LocationPermissionIssue.NONE

            onPermissionsReady()

        } else {

            permissionIssue =
                LocationPermissionIssue.BACKGROUND_MISSING

            showBackgroundExplanation = true
        }
    }

    val foregroundPermissionLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestMultiplePermissions()
        ) {

            if (hasForegroundPermission()) {

                continueAfterForegroundPermission()

            } else {

                val shouldShowFineRationale =
                    activity?.let {
                        ActivityCompat
                            .shouldShowRequestPermissionRationale(
                                it,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                    } ?: false

                val shouldShowCoarseRationale =
                    activity?.let {
                        ActivityCompat
                            .shouldShowRequestPermissionRationale(
                                it,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                    } ?: false

                if (
                    shouldShowFineRationale ||
                    shouldShowCoarseRationale
                ) {

                    permissionIssue =
                        LocationPermissionIssue.FOREGROUND_DENIED

                } else {

                    permissionIssue =
                        LocationPermissionIssue.FOREGROUND_BLOCKED
                }
            }
        }

    fun requestEnableLocationSharing() {

        if (hasForegroundPermission()) {

            continueAfterForegroundPermission()
            return
        }

        if (
            permissionIssue ==
            LocationPermissionIssue.FOREGROUND_BLOCKED
        ) {

            showBlockedExplanation = true
            return
        }

        foregroundPermissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    DisposableEffect(lifecycleOwner) {

        val observer =
            LifecycleEventObserver { _, event ->

                if (
                    event == Lifecycle.Event.ON_RESUME &&
                    waitingForSettingsResult
                ) {

                    waitingForSettingsResult = false

                    if (hasForegroundPermission()) {

                        continueAfterForegroundPermission()

                    } else {

                        permissionIssue =
                            LocationPermissionIssue.FOREGROUND_BLOCKED
                    }
                }
            }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    content(
        ::requestEnableLocationSharing,
        permissionIssue
    )

    if (showBackgroundExplanation) {

        AlertDialog(
            onDismissRequest = {
                showBackgroundExplanation = false
            },

            title = {
                Text("Allow background location")
            },

            text = {
                Text(
                    "MapLink needs background location access " +
                            "so your accepted friends can receive updated " +
                            "location information while MapLink is minimized. " +
                            "On the next screen, open Permissions → Location " +
                            "and select Allow all the time."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        showBackgroundExplanation = false
                        waitingForSettingsResult = true

                        openAppSettings(context)
                    }
                ) {
                    Text("Open Settings")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showBackgroundExplanation = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showBlockedExplanation) {

        AlertDialog(
            onDismissRequest = {
                showBlockedExplanation = false
            },

            title = {
                Text("Location permission blocked")
            },

            text = {
                Text(
                    "MapLink cannot request location permission again. " +
                            "Open Android Settings and allow location access " +
                            "to enable location sharing."
                )
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        showBlockedExplanation = false
                        waitingForSettingsResult = true

                        openAppSettings(context)
                    }
                ) {
                    Text("Open Settings")
                }
            },

            dismissButton = {

                TextButton(
                    onClick = {
                        showBlockedExplanation = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}

private fun Context.findActivity(): Activity? {

    var currentContext = this

    while (currentContext is ContextWrapper) {

        if (currentContext is Activity) {
            return currentContext
        }

        currentContext = currentContext.baseContext
    }

    return null
}

private fun openAppSettings(
    context: Context
) {

    val intent =
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts(
                "package",
                context.packageName,
                null
            )
        )

    context.startActivity(intent)
}
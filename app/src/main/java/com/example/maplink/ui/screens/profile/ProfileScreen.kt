package com.example.maplink.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileScreen(
    locationSharingEnabled: Boolean,
    onLocationSharingChanged: (Boolean) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Profile",
            style = MaterialTheme.typography.headlineLarge
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "Share Location",
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text =
                        if (locationSharingEnabled) {
                            "Your location is being shared with friends"
                        } else {
                            "Location sharing is disabled"
                        },
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Switch(
                checked = locationSharingEnabled,
                onCheckedChange = onLocationSharingChanged
            )
        }
    }
}
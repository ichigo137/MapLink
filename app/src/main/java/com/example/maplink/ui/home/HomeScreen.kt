package com.example.maplink.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onShareLocationClick: () -> Unit = {}
) {

    var link by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            "MapLink",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(Modifier.height(12.dp))

        Text(
            "Share locations instantly."
        )

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = onShareLocationClick,
            modifier = Modifier.fillMaxWidth()
        ) {

            Icon(Icons.Default.LocationOn, null)

            Spacer(Modifier.width(8.dp))

            Text("Share My Location")
        }

        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = link,
            onValueChange = {
                link = it
            },
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Paste MapLink")
            }
        )

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { },
            modifier = Modifier.fillMaxWidth()
        ) {

            Icon(Icons.Default.Share, null)

            Spacer(Modifier.width(8.dp))

            Text("Open Link")
        }

    }

}
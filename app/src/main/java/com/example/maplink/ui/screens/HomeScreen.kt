package com.example.maplink.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onShareLocation: () -> Unit
) {

    var link by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "🗺️ MapLink",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(
            onClick = onShareLocation,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Share My Location")
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = link,
            onValueChange = { link = it },
            label = {
                Text("Paste MapLink")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // We'll implement this later
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open Link")
        }
    }
}
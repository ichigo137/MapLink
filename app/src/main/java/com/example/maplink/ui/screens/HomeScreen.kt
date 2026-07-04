package com.example.maplink.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.maplink.navigation.Routes

@Composable
fun HomeScreen(
    onOpenMap: () -> Unit,
    onSearchUsers: () -> Unit,
    onFriendRequests: () -> Unit,
    onFriends: () -> Unit,
    onProfile: () -> Unit
) {

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

        Spacer(Modifier.height(40.dp))

        Button(
            onClick = onSearchUsers,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🔍 Search Users")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onFriendRequests,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("👥 Friend Requests")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onOpenMap,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("🗺️ Open Map")
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = onProfile,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("👤 Profile")
        }

        Button(
            onClick = onFriends,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("❤️ Friends")
        }

    }
}
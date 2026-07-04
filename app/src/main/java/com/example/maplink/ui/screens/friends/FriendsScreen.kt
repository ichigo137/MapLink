package com.example.maplink.ui.screens.friends

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FriendsScreen(
    viewModel: FriendsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    val friends by viewModel.friends.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "❤️ Friends",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {

            items(friends) { friend ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(friend.name)

                        Text("@${friend.username}")

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            if (friend.online)
                                "🟢 Online"
                            else
                                "⚪ Offline"
                        )
                    }
                }
            }
        }
    }
}
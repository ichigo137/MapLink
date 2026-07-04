package com.example.maplink.data.repository

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.maplink.ui.screens.requests.FriendRequestsViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun FriendRequestsScreen() {

    val vm: FriendRequestsViewModel = viewModel()
    val requests by vm.requests.collectAsState()

    val uid = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(uid) {
        uid?.let {
            vm.loadRequests(it)
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {

        items(requests) { item ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = item.senderName,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "@${item.senderUsername}",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {

                        Button(
                            onClick = {
                                vm.accept(item.request)
                            }
                        ) {
                            Text("Accept")
                        }

                        OutlinedButton(
                            onClick = {
                                vm.reject(item.request)
                            }
                        ) {
                            Text("Reject")
                        }
                    }
                }
            }
        }
    }
}
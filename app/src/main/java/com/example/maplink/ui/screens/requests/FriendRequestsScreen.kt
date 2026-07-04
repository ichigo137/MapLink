package com.example.maplink.ui.screens.requests
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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

        items(requests) { request ->

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {

                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Text(
                        text = request.senderName,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text("@${request.senderUsername}")

                }
            }
        }
    }
}
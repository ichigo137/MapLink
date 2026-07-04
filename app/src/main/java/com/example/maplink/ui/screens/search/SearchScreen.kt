package com.example.maplink.ui.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
@Composable
fun SearchScreen() {

    val vm: SearchViewModel = viewModel()

    var query by remember {
        mutableStateOf("")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        OutlinedTextField(
            value = query,
            onValueChange = {

                query = it
                vm.search(it)

            },
            label = {
                Text("Search users")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        LazyColumn {

            items(vm.users) { user ->

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {

                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text("@${user.username}")

                        Spacer(Modifier.height(8.dp))

                        Button(
                            onClick = {

                                val currentUid =
                                    FirebaseAuth.getInstance().currentUser?.uid ?: return@Button

                                vm.sendFriendRequest(
                                    senderUid = currentUid,
                                    receiverUid = user.uid
                                )

                            }
                        ) {

                            Text("Add Friend")

                        }

                    }

                }

            }

        }

    }

}
package com.example.maplink.ui.search

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.maplink.data.model.User
import com.example.maplink.data.repository.UserRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.maplink.data.repository.FriendRequestRepository
class SearchViewModel : ViewModel() {

    private val repository = UserRepository()
    private val friendRequestRepository = FriendRequestRepository()

    val users = mutableStateListOf<User>()

    fun search(query: String) {

        repository.searchUsers(
            query = query,

            onResult = {

                users.clear()
                users.addAll(it)

            },

            onFailure = {

            }
        )
    }

    fun sendFriendRequest(
        senderUid: String,
        receiverUid: String
    ) {

        viewModelScope.launch {

            friendRequestRepository.sendFriendRequest(
                senderUid,
                receiverUid
            )

        }

    }
}
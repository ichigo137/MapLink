package com.example.maplink.ui.screens.friends

import androidx.lifecycle.ViewModel
import com.example.maplink.data.repository.Friend
import com.example.maplink.data.repository.FriendsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FriendsViewModel : ViewModel() {

    private val repository = FriendsRepository()

    private val _friends = MutableStateFlow<List<Friend>>(emptyList())
    val friends: StateFlow<List<Friend>> = _friends

    init {
        observeFriends()
    }

    private fun observeFriends() {
        repository.observeFriends { friends ->
            _friends.value = friends
        }
    }
}
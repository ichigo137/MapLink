package com.example.maplink.ui.screens.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maplink.data.repository.Friend
import com.example.maplink.data.repository.FriendsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FriendsViewModel : ViewModel() {

    private val repository = FriendsRepository()

    private val _friends = MutableStateFlow<List<Friend>>(emptyList())
    val friends: StateFlow<List<Friend>> = _friends

    init {
        loadFriends()
    }

    fun loadFriends() {
        viewModelScope.launch {
            _friends.value = repository.getFriends()
        }
    }
}
package com.example.maplink.ui.screens.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maplink.data.model.FriendRequestItem
import com.example.maplink.data.repository.FriendRequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FriendRequestsViewModel : ViewModel() {

    private val repository = FriendRequestRepository()

    private val _requests =
        MutableStateFlow<List<FriendRequestItem>>(emptyList())

    val requests: StateFlow<List<FriendRequestItem>> = _requests

    /*fun loadRequests(uid: String) {

        viewModelScope.launch {

            _requests.value =
                repository.getIncomingRequests(uid)

        }
    }*/

    fun loadRequests(uid: String) {

        android.util.Log.d("MapLink", "loadRequests() called")

        viewModelScope.launch {

            _requests.value = repository.getIncomingRequests(uid)

            android.util.Log.d(
                "MapLink",
                "Loaded ${_requests.value.size} requests"
            )
        }
    }
}
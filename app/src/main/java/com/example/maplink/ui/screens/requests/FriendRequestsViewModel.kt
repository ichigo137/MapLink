package com.example.maplink.ui.screens.requests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maplink.data.model.FriendRequest
import com.example.maplink.data.repository.FriendRequestItem
import com.example.maplink.data.repository.FriendRequestRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FriendRequestsViewModel : ViewModel() {

    private val repository = FriendRequestRepository()

    private val _requests =
        MutableStateFlow<List<FriendRequestItem>>(emptyList())

    val requests: StateFlow<List<FriendRequestItem>> = _requests

    private var currentUid: String? = null

    fun loadRequests(uid: String) {

        currentUid = uid

        viewModelScope.launch {

            _requests.value = repository.getIncomingRequests(uid)

            android.util.Log.d(
                "MapLink",
                "Loaded ${_requests.value.size} requests"
            )
        }
    }

    fun accept(request: FriendRequest) {

        viewModelScope.launch {

            repository.acceptRequest(request)

            currentUid?.let {
                loadRequests(it)
            }

        }
    }

    fun reject(request: FriendRequest) {

        viewModelScope.launch {

            repository.rejectRequest(request.id)

            currentUid?.let {
                loadRequests(it)
            }

        }
    }
}
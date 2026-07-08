package com.example.maplink.ui.screens.friends

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.maplink.data.repository.Friend
import com.example.maplink.data.repository.FriendsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FriendsViewModel : ViewModel() {

    companion object {
        private const val ONLINE_THRESHOLD_MS = 30_000L
        private const val PRESENCE_REFRESH_INTERVAL_MS = 5_000L
    }

    private val repository = FriendsRepository()

    private var rawFriends: List<Friend> = emptyList()

    private val _friends =
        MutableStateFlow<List<Friend>>(emptyList())

    val friends: StateFlow<List<Friend>> = _friends

    init {
        observeFriends()
        startPresenceRefresh()
    }

    private fun observeFriends() {

        repository.observeFriends { friends ->

            rawFriends = friends

            updateEffectivePresence()
        }
    }

    private fun startPresenceRefresh() {

        viewModelScope.launch {

            while (true) {

                updateEffectivePresence()

                delay(PRESENCE_REFRESH_INTERVAL_MS)
            }
        }
    }

    private fun updateEffectivePresence() {

        val currentTime = System.currentTimeMillis()

        _friends.value = rawFriends.map { friend ->

            val lastUpdatedMillis =
                friend.lastUpdated?.toDate()?.time

            val isFresh =
                lastUpdatedMillis != null &&
                        currentTime - lastUpdatedMillis <= ONLINE_THRESHOLD_MS

            friend.copy(
                online = friend.online && isFresh
            )
        }
    }
}
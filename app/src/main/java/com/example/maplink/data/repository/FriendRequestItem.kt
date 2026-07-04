package com.example.maplink.data.repository

import com.example.maplink.data.model.FriendRequest

data class FriendRequestItem(
    val request: FriendRequest,
    val senderName: String,
    val senderUsername: String
)
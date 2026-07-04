package com.example.maplink.data.model

import com.google.firebase.Timestamp

data class FriendRequest(
    val id: String = "",
    val senderUid: String = "",
    val receiverUid: String = "",
    val status: String = "pending",
    val createdAt: Timestamp? = null
)
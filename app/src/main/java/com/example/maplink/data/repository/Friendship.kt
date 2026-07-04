package com.example.maplink.data.repository.model

import com.google.firebase.Timestamp

data class Friendship(
    val id: String = "",
    val user1: String = "",
    val user2: String = "",
    val createdAt: Timestamp? = null
)
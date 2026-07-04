package com.example.maplink.data.model

import com.google.firebase.Timestamp

data class User(
    val uid: String = "",
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val online: Boolean = false,
    val createdAt: Timestamp? = null   // ← use Timestamp instead of Any?
)
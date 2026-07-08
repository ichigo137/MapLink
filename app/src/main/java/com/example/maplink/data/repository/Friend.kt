package com.example.maplink.data.repository

import com.google.firebase.Timestamp

data class Friend(
    val uid: String = "",
    val name: String = "",
    val username: String = "",
    val online: Boolean = false,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val lastUpdated: Timestamp? = null
)
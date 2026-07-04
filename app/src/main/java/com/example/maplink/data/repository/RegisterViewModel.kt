package com.example.maplink.ui.auth.register

import androidx.lifecycle.ViewModel
import com.example.maplink.data.repository.AuthRepository

class RegisterViewModel : ViewModel() {

    private val repository = AuthRepository()

    fun register(
        name: String,
        username: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {

        repository.register(
            name = name,
            username = username,
            email = email,
            password = password,
            onSuccess = onSuccess,
            onFailure = onFailure
        )
    }
}
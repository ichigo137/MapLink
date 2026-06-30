package com.example.maplink.ui.auth.register

import androidx.lifecycle.ViewModel
import com.example.maplink.data.repository.AuthRepository

class RegisterViewModel : ViewModel() {

    private val repository = AuthRepository()

    fun register(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {

        repository.register(
            email,
            password,
            onSuccess,
            onFailure
        )

    }

}
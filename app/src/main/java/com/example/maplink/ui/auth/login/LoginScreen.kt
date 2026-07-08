package com.example.maplink.ui.auth.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.maplink.data.credential.PasswordCredentialManager
import com.example.maplink.data.repository.AuthRepository
import com.example.maplink.service.LocationServiceManager
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLogin: () -> Unit,
    onRegister: () -> Unit
) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var email by remember {
        mutableStateOf("")
    }

    var password by remember {
        mutableStateOf("")
    }

    var error by remember {
        mutableStateOf<String?>(null)
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    val authRepository = remember {
        AuthRepository()
    }

    fun login(
        loginEmail: String,
        loginPassword: String,
        saveCredential: Boolean
    ) {

        if (isLoading) {
            return
        }

        isLoading = true
        error = null

        authRepository.login(
            email = loginEmail,
            password = loginPassword,

            onSuccess = {

                coroutineScope.launch {

                    if (saveCredential) {

                        PasswordCredentialManager.savePassword(
                            context = context,
                            email = loginEmail,
                            password = loginPassword
                        )
                    }

                    LocationServiceManager
                        .startIfAllowed(context)

                    isLoading = false

                    onLogin()
                }
            },

            onFailure = {

                isLoading = false
                error = it
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),

        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "MapLink",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(Modifier.height(32.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = {
                Text("Email")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
            },
            label = {
                Text("Password")
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = !isLoading,
            visualTransformation =
                PasswordVisualTransformation()
        )

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = {

                if (
                    email.isBlank() ||
                    password.isBlank()
                ) {
                    error = "Enter your email and password."
                    return@Button
                }

                login(
                    loginEmail = email.trim(),
                    loginPassword = password,
                    saveCredential = true
                )
            },

            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (isLoading) {
                    "Logging in..."
                } else {
                    "Login"
                }
            )
        }

        Spacer(Modifier.height(8.dp))

        TextButton(
            enabled = !isLoading,

            onClick = {

                coroutineScope.launch {

                    error = null

                    val credential =
                        PasswordCredentialManager
                            .getSavedPassword(context)

                    if (credential == null) {

                        error =
                            "No saved password selected."

                        return@launch
                    }

                    email = credential.email
                    password = credential.password

                    login(
                        loginEmail = credential.email,
                        loginPassword = credential.password,
                        saveCredential = false
                    )
                }
            }
        ) {
            Text("Use saved password")
        }

        error?.let {

            Spacer(Modifier.height(8.dp))

            Text(
                text = it,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(12.dp))

        TextButton(
            enabled = !isLoading,
            onClick = onRegister
        ) {
            Text("Create an account")
        }
    }
}
package com.example.maplink.ui.auth.register

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun RegisterScreen(
    onRegister: (String, String, String) -> Unit,
    onLogin: () -> Unit = {}
) {

    val viewModel: RegisterViewModel = viewModel()

    var name by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it.lowercase().replace(" ", "")
            },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {

                errorMessage = ""

                if (name.isBlank() ||
                    username.isBlank() ||
                    email.isBlank() ||
                    password.isBlank() ||
                    confirmPassword.isBlank()
                ) {
                    errorMessage = "Please fill all fields."
                    return@Button
                }

                if (password != confirmPassword) {
                    errorMessage = "Passwords do not match."
                    return@Button
                }

                viewModel.register(
                    name = name,
                    username = username,
                    email = email,
                    password = password,
                    onSuccess = {
                        onRegister(name, email, password)
                    },
                    onFailure = {
                        errorMessage = it
                    }
                )
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Create Account")
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = onLogin
        ) {
            Text("Already have an account? Login")
        }
    }
}
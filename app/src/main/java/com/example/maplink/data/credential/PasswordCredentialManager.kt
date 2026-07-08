package com.example.maplink.data.credential

import android.content.Context
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.PasswordCredential
import androidx.credentials.GetPasswordOption
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialException

data class SavedPasswordCredential(
    val email: String,
    val password: String
)

object PasswordCredentialManager {

    suspend fun savePassword(
        context: Context,
        email: String,
        password: String
    ): Boolean {

        val credentialManager =
            CredentialManager.create(context)

        val request =
            CreatePasswordRequest(
                id = email,
                password = password
            )

        return try {

            credentialManager.createCredential(
                context = context,
                request = request
            )

            true

        } catch (exception: CreateCredentialException) {

            false
        }
    }

    suspend fun getSavedPassword(
        context: Context
    ): SavedPasswordCredential? {

        val credentialManager =
            CredentialManager.create(context)

        val request =
            GetCredentialRequest(
                credentialOptions = listOf(
                    GetPasswordOption()
                )
            )

        return try {

            val result =
                credentialManager.getCredential(
                    context = context,
                    request = request
                )

            val credential = result.credential

            if (credential is PasswordCredential) {

                SavedPasswordCredential(
                    email = credential.id,
                    password = credential.password
                )

            } else {

                null
            }

        } catch (exception: GetCredentialException) {

            null
        }
    }
}
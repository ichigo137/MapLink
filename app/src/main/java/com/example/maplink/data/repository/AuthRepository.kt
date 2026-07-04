package com.example.maplink.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun register(
        name: String,
        username: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {


        firestore.collection("users")
            .whereEqualTo("username", username)
            .get()
            .addOnSuccessListener { documents ->

                if (!documents.isEmpty) {
                    onFailure("Username already exists")
                    return@addOnSuccessListener
                }

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener {

                        val firebaseUser = auth.currentUser

                        if (firebaseUser == null) {
                            onFailure("User creation failed")
                            return@addOnSuccessListener
                        }

                        val user = hashMapOf(
                            "uid" to firebaseUser.uid,
                            "name" to name,
                            "username" to username,
                            "email" to email,
                            "latitude" to 0.0,
                            "longitude" to 0.0,
                            "online" to true,
                            "createdAt" to FieldValue.serverTimestamp()
                        )

                        firestore.collection("users")
                            .document(firebaseUser.uid)
                            .set(user)
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener {
                                onFailure(it.message ?: "Firestore error")
                            }

                    }
                    .addOnFailureListener {
                        onFailure(it.message ?: "Unknown error")
                    }

            }
            .addOnFailureListener {
                onFailure(it.message ?: "Couldn't verify username")
            }
    }

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it.message ?: "Login failed")
            }
    }
}